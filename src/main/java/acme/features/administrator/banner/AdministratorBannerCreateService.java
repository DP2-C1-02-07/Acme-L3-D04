
package acme.features.administrator.banner;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.Banner;
import acme.framework.components.accounts.Administrator;
import acme.framework.components.models.Tuple;
import acme.framework.helpers.MomentHelper;
import acme.framework.services.AbstractService;

@Service
public class AdministratorBannerCreateService extends AbstractService<Administrator, Banner> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AdministratorBannerRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void check() {
		super.getResponse().setChecked(true);
	}

	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Banner object;
		Date moment;

		moment = MomentHelper.getCurrentMoment();
		object = new Banner();

		object.setInstantationMoment(moment);
		object.setSlogan("");
		object.setPictureLink("");
		object.setDocumentLink("");

		super.getBuffer().setData(object);
	}

	@Override
	public void bind(final Banner object) {
		assert object != null;

		super.bind(object, "instantationMoment", "displayStartMoment", "displayEndMoment", "pictureLink", "slogan", "documentLink");

	}

	@Override
	public void validate(final Banner object) {
		assert object != null;

		if (!super.getBuffer().getErrors().hasErrors("displayStartMoment") || !super.getBuffer().getErrors().hasErrors("displayEndMoment")) {

			boolean validMoment;
			validMoment = MomentHelper.isAfterOrEqual(object.getDisplayStartMoment(), object.getInstantationMoment());
			super.state(validMoment, "displayStartMoment", "administrator.banner.post.after-instantiation");

			boolean validPeriod;
			validPeriod = MomentHelper.isLongEnough(object.getDisplayEndMoment(), object.getDisplayStartMoment(), 7, ChronoUnit.DAYS);
			super.state(validPeriod, "displayEndMoment", "administrator.banner.post.one-week");

			boolean endAfterStart;
			endAfterStart = MomentHelper.isAfter(object.getDisplayEndMoment(), object.getDisplayStartMoment());
			super.state(endAfterStart, "displayEndMoment", "administrator.banner.post.after-display");
		}
	}

	@Override
	public void perform(final Banner object) {
		assert object != null;
		Date moment;

		moment = MomentHelper.getCurrentMoment();
		object.setInstantationMoment(moment);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Banner object) {
		assert object != null;
		Tuple tuple;
		tuple = super.unbind(object, "instantationMoment", "displayStartMoment", "displayEndMoment", "pictureLink", "slogan", "documentLink");
		super.getResponse().setData(tuple);
	}
}
