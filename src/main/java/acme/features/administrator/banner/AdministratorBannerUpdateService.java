
package acme.features.administrator.banner;

import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.components.SpamDetector;
import acme.entities.Banner;
import acme.framework.components.accounts.Administrator;
import acme.framework.components.models.Tuple;
import acme.framework.helpers.MomentHelper;
import acme.framework.services.AbstractService;

@Service
public class AdministratorBannerUpdateService extends AbstractService<Administrator, Banner> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AdministratorBannerRepository repository;

	// AbstractService interface -------------------------------------


	@Override
	public void check() {
		final boolean status = super.getRequest().hasData("id", int.class);
		super.getResponse().setChecked(status);
	}

	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		Banner object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOneBannerById(id);

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
		Date moment;
		final SpamDetector detector = new SpamDetector();

		moment = MomentHelper.getCurrentMoment();

		if (!super.getBuffer().getErrors().hasErrors("displayStartMoment")) {
			boolean validMoment;
			validMoment = MomentHelper.isAfterOrEqual(object.getDisplayStartMoment(), moment);
			super.state(validMoment, "displayStartMoment", "administrator.banner.post.after-update");
		}

		if (!super.getBuffer().getErrors().hasErrors("displayStartMoment") && !super.getBuffer().getErrors().hasErrors("displayEndMoment")) {

			boolean validPeriod;
			validPeriod = MomentHelper.isLongEnough(object.getDisplayEndMoment(), object.getDisplayStartMoment(), 7, ChronoUnit.DAYS);
			super.state(validPeriod, "displayEndMoment", "administrator.banner.post.one-week");

			boolean endAfterStart;
			endAfterStart = MomentHelper.isAfter(object.getDisplayEndMoment(), object.getDisplayStartMoment());
			super.state(endAfterStart, "displayEndMoment", "administrator.banner.post.after-display");
		}

		final boolean pictureLinkhasSpam = !detector.scanString(super.getRequest().getData("pictureLink", String.class));
		super.state(pictureLinkhasSpam, "pictureLink", "javax.validation.constraints.HasSpam.message");

		final boolean documentLinkhasSpam = !detector.scanString(super.getRequest().getData("documentLink", String.class));
		super.state(documentLinkhasSpam, "documentLink", "javax.validation.constraints.HasSpam.message");

		final boolean slogan = !detector.scanString(super.getRequest().getData("slogan", String.class));
		super.state(slogan, "slogan", "javax.validation.constraints.HasSpam.message");
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
		Date moment;

		moment = MomentHelper.getCurrentMoment();
		object.setInstantationMoment(moment);

		final Tuple tuple = super.unbind(object, "instantationMoment", "displayStartMoment", "displayEndMoment", "pictureLink", "slogan", "documentLink");
		super.getResponse().setData(tuple);
	}

}
