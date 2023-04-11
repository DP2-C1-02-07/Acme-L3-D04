
package acme.features.authenticated.auditor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.framework.components.accounts.Authenticated;
import acme.framework.components.accounts.Principal;
import acme.framework.components.accounts.UserAccount;
import acme.framework.components.models.Tuple;
import acme.framework.controllers.HttpMethod;
import acme.framework.helpers.PrincipalHelper;
import acme.framework.services.AbstractService;
import acme.roles.Auditor;

@Service
public class AuthenticatedAuditorCreateService extends AbstractService<Authenticated, Auditor> {
	// Internal state ---------------------------------------------------------

	@Autowired
	protected AuthenticatedAuditorRepository repository;


	@Override
	public void authorise() {
		final boolean status = !super.getRequest().getPrincipal().hasRole(Auditor.class);
		super.getResponse().setAuthorised(status);
	}

	@Override
	public void check() {
		super.getResponse().setChecked(true);
	}

	@Override
	public void load() {
		final Auditor object = new Auditor();
		final Principal principal = super.getRequest().getPrincipal();
		final int userAccountId = principal.getAccountId();
		final UserAccount userAccount = this.repository.findOneUserAccountById(userAccountId);

		object.setUserAccount(userAccount);
		super.getBuffer().setData(object);
	}

	@Override
	public void bind(final Auditor object) {
		assert object != null;
		super.bind(object, "firm", "professionalId", "certifications", "link");
	}

	@Override
	public void validate(final Auditor object) {
		assert object != null;
	}

	@Override
	public void perform(final Auditor object) {
		assert object != null;
		this.repository.save(object);
	}

	@Override
	public void unbind(final Auditor object) {
		final Tuple tuple = super.unbind(object, "firm", "professionalId", "certifications", "link");
		super.getResponse().setData(tuple);
	}

	@Override
	public void onSuccess() {
		if (super.getRequest().getMethod().equals(HttpMethod.POST))
			PrincipalHelper.handleUpdate();
	}
}