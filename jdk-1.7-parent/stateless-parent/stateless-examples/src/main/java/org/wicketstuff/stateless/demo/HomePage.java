package org.wicketstuff.stateless.demo;

import java.util.Arrays;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.devutils.stateless.StatelessComponent;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.StatelessForm;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.wicketstuff.stateless.StatelessAjaxFallbackLink;
import org.wicketstuff.stateless.StatelessAjaxFormComponentUpdatingBehavior;
import org.wicketstuff.stateless.StatelessAjaxSubmitLink;

/**
 * For testing only
 */
@StatelessComponent
public class HomePage extends WebPage {

	private static final String COUNTER_PARAM = "counter";

	/**
	 * Constructor that is invoked when page is invoked without a session.
	 *
	 * @param parameters
	 *            Page parameters
	 */
	public HomePage(final PageParameters parameters) {
		final Label c2 = new Label("c2", new AbstractReadOnlyModel<Integer>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Integer getObject() {
				final String counter = getParameter(parameters, COUNTER_PARAM);
				return counter != null ? Integer.parseInt(counter) : 0;
			}

		});
		final Link<?> c2Link = new StatelessAjaxFallbackLink<Void>("c2-link",
				null, parameters) {

			@Override
			public void onClick(final AjaxRequestTarget target) {
				if (target != null) {
					Integer counter = (Integer) c2.getDefaultModelObject();
					System.out.println("label: " + counter);
					updateParams(getPageParameters(), counter);
					target.add(c2);
				}
			}
		};

		add(c2Link);
		add(c2.setOutputMarkupId(true));

		final String _a = getParameter(parameters, "a");
		final String _b = getParameter(parameters, "b");
		
		final TextField<String> a = new TextField<String>("a",
			new Model<String>(_a));
		final TextField<String> b = new TextField<String>("b",
			new Model<String>(_b));

		final Form<String> form = new StatelessForm<String>("inputForm") {

			@Override
			protected void onSubmit() {
				System.out.format("clicked sumbit: a = [%s], b = [%s]%n",
						a.getModelObject(), b.getModelObject());
			}
			
		};
		final DropDownChoice<String> c = new DropDownChoice<String>("c",
				new Model<String>("2"), Arrays.asList(new String[] { "1", "2",
						"3" }));

		c.add(new StatelessAjaxFormComponentUpdatingBehavior("change") {

			@Override
			protected PageParameters getPageParameters() {
				return new PageParameters();
			}

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				final String value = c.getModelObject();
				System.out.println("xxxxxxxxxxxxxxxxxx: " + value);
			}
		});
		
		form.add(a.setRequired(true));
		form.add(b.setRequired(true));
		
		final Component feedback = new FeedbackPanel("feedback");
		
		form.add(feedback.setOutputMarkupId(true));
		
		form.add(new StatelessAjaxSubmitLink("submit"){
			@Override
			protected void onError(AjaxRequestTarget target)
			{
				super.onError(target);
				target.add(feedback);
			}
			@Override
			protected void onSubmit(AjaxRequestTarget target)
			{		
				super.onSubmit(target);
				target.add(feedback);
			}
		});
		
		add(form);

		add(c);
	}

	private String getParameter(final PageParameters parameters,
			final String key) {
		final StringValue value = parameters.get(key);

		if (value.isNull() || value.isEmpty()) {
			return null;
		}

		return value.toString();
	}
	
	protected final void updateParams(final PageParameters pageParameters, final int counter) {
		pageParameters.set(COUNTER_PARAM, Integer.toString(counter + 1));
	}
}
