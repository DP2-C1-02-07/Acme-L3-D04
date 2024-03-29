
package acme.features.auditor.audit;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.components.SpamDetector;
import acme.entities.Audit;
import acme.entities.Course;
import acme.framework.components.jsp.SelectChoices;
import acme.framework.components.models.Tuple;
import acme.framework.services.AbstractService;
import acme.roles.Auditor;

@Service
public class AuditorAuditPublishService extends AbstractService<Auditor, Audit> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AuditorAuditRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void check() {
		final boolean status = super.getRequest().hasData("id", int.class);

		super.getResponse().setChecked(status);
	}

	@Override
	public void authorise() {
		final int auditId = super.getRequest().getData("id", int.class);
		final Audit audit = this.repository.findOneAuditById(auditId);
		final Auditor auditor = audit == null ? null : audit.getAuditor();
		final boolean status = audit != null && audit.isDraftMode() && super.getRequest().getPrincipal().hasRole(auditor);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		final int id = super.getRequest().getData("id", int.class);
		final Audit object = this.repository.findOneAuditById(id);

		super.getBuffer().setData(object);
	}

	@Override
	public void bind(final Audit object) {
		assert object != null;
		final int courseId = super.getRequest().getData("course", int.class);
		final Course course = this.repository.findOneCourseById(courseId);
		//		final Optional<Course> firstCourse = course.stream().findFirst();
		//		assert firstCourse.isPresent();

		super.bind(object, "code", "conclusion", "strongPoints", "weakPoints");
		object.setCourse(course);
	}

	@Override
	public void validate(final Audit object) {
		assert object != null;

		final SpamDetector detector = new SpamDetector();

		final boolean conclusionHasSpam = !detector.scanString(super.getRequest().getData("conclusion", String.class));
		super.state(conclusionHasSpam, "conclusion", "javax.validation.constraints.HasSpam.message");

		final boolean strongPointsHasSpam = !detector.scanString(super.getRequest().getData("strongPoints", String.class));
		super.state(strongPointsHasSpam, "strongPoints", "javax.validation.constraints.HasSpam.message");

		final boolean weakPointsHasSpam = !detector.scanString(super.getRequest().getData("weakPoints", String.class));
		super.state(weakPointsHasSpam, "weakPoints", "javax.validation.constraints.HasSpam.message");
	}

	@Override
	public void perform(final Audit object) {
		assert object != null;

		object.setDraftMode(false);
		this.repository.save(object);
	}

	@Override
	public void unbind(final Audit object) {
		assert object != null;
		final Collection<Course> courses = this.repository.findAllCourses();
		final SelectChoices choices = SelectChoices.from(courses, "code", object.getCourse());
		final Tuple tuple = super.unbind(object, "code", "conclusion", "strongPoints", "weakPoints", "draftMode");
		tuple.put("course", choices.getSelected().getKey());
		tuple.put("courses", choices);

		super.getResponse().setData(tuple);
	}

}
