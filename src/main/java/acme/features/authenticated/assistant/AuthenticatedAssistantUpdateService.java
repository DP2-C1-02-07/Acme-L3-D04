
package acme.features.authenticated.assistant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.components.SpamDetector;
import acme.framework.components.accounts.Authenticated;
import acme.framework.components.accounts.Principal;
import acme.framework.components.models.Tuple;
import acme.framework.controllers.HttpMethod;
import acme.framework.helpers.PrincipalHelper;
import acme.framework.services.AbstractService;
import acme.roles.Assistant;

@Service
public class AuthenticatedAssistantUpdateService extends AbstractService<Authenticated, Assistant> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AuthenticatedAssistantRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void check() {
		super.getResponse().setChecked(true);
	}

	@Override
	public void load() {
		Assistant object;
		int userAccountId;
		Principal principal;
		principal = super.getRequest().getPrincipal();
		userAccountId = principal.getAccountId();
		object = this.repository.findOneAssistantByUserAccountId(userAccountId);
		super.getBuffer().setData(object);
	}

	@Override
	public void bind(final Assistant object) {
		assert object != null;
		super.bind(object, "supervisor", "expertiseFields", "resume", "link");
	}

	@Override
	public void validate(final Assistant object) {
		assert object != null;

		final SpamDetector detector = new SpamDetector();

		final boolean supervisorHasSpam = !detector.scanString(super.getRequest().getData("supervisor", String.class));
		super.state(supervisorHasSpam, "supervisor", "javax.validation.constraints.HasSpam.message");

		final boolean expertiseFieldsHasSpam = !detector.scanString(super.getRequest().getData("expertiseFields", String.class));
		super.state(expertiseFieldsHasSpam, "expertiseFields", "javax.validation.constraints.HasSpam.message");

		final boolean resumeHasSpam = !detector.scanString(super.getRequest().getData("resume", String.class));
		super.state(resumeHasSpam, "resume", "javax.validation.constraints.HasSpam.message");

		final boolean linkHasSpam = !detector.scanString(super.getRequest().getData("link", String.class));
		super.state(linkHasSpam, "link", "javax.validation.constraints.HasSpam.message");
	}

	@Override
	public void perform(final Assistant object) {
		assert object != null;
		this.repository.save(object);
	}

	@Override
	public void unbind(final Assistant object) {
		assert object != null;
		Tuple tuple;
		tuple = super.unbind(object, "supervisor", "expertiseFields", "resume", "link");
		super.getResponse().setData(tuple);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals(HttpMethod.POST))
			PrincipalHelper.handleUpdate();
	}

}
