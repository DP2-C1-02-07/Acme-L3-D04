
package acme.features.company.practicum;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.components.SpamDetector;
import acme.entities.Course;
import acme.entities.Practicum;
import acme.entities.Session;
import acme.framework.components.jsp.SelectChoices;
import acme.framework.components.models.Tuple;
import acme.framework.services.AbstractService;
import acme.roles.Company;

@Service
public class CompanyPracticumPublishService extends AbstractService<Company, Practicum> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected CompanyPracticumRepository repository;

	// AbstractService interface ----------------------------------------------


	@Override
	public void check() {
		boolean status;

		status = super.getRequest().hasData("id", int.class);

		super.getResponse().setChecked(status);
	}

	@Override
	public void authorise() {
		boolean status;
		int practicumId;
		Practicum practicum;
		Company company;

		practicumId = super.getRequest().getData("id", int.class);
		practicum = this.repository.findOnePracticaById(practicumId);
		company = practicum == null ? null : practicum.getCompany();
		status = practicum != null && practicum.isDraftMode() && super.getRequest().getPrincipal().hasRole(company);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Practicum object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOnePracticaById(id);

		super.getBuffer().setData(object);
	}

	@Override
	public void bind(final Practicum object) {
		assert object != null;

		int courseId;
		Course course;

		courseId = super.getRequest().getData("course", int.class);
		course = this.repository.findOneCourseById(courseId);

		super.bind(object, "code", "title", "abstractThing", "goals", "estimatedTime");

		object.setCourse(course);

	}

	@Override
	public void validate(final Practicum object) {
		assert object != null;

		final SpamDetector detector = new SpamDetector();

		final boolean titleHasSpam = !detector.scanString(super.getRequest().getData("title", String.class));
		super.state(titleHasSpam, "title", "javax.validation.constraints.HasSpam.message");

		final boolean abstractThingHasSpam = !detector.scanString(super.getRequest().getData("abstractThing", String.class));
		super.state(abstractThingHasSpam, "abstractThing", "javax.validation.constraints.HasSpam.message");

		final boolean goalsHasSpam = !detector.scanString(super.getRequest().getData("goals", String.class));
		super.state(goalsHasSpam, "goals", "javax.validation.constraints.HasSpam.message");

		//Duplicate code validation

		if (!super.getBuffer().getErrors().hasErrors("code")) {
			Practicum existing;

			existing = this.repository.findOnePracticaByCode(object.getCode());
			super.state(existing == null || object.equals(existing), "code", "company.practicum.form.error.duplicated");

		}
	}

	@Override
	public void perform(final Practicum object) {
		assert object != null;
		object.setDraftMode(false);

		final Collection<Session> sessions = this.repository.findSessionsByPracticumId(object.getId());
		for (final Session s : sessions) {
			s.setDraftMode(false);
			this.repository.save(s);
		}

		this.repository.save(object);
	}

	@Override
	public void unbind(final Practicum object) {
		assert object != null;
		final Collection<Course> courses;
		final SelectChoices choices;

		courses = this.repository.findAllCourses();
		choices = SelectChoices.from(courses, "code", object.getCourse());

		final Collection<Session> sessions = this.repository.findSessionsByPracticumId(object.getId());
		final Double estimatedTime = object.estimatedTime(sessions);
		Tuple tuple;

		tuple = super.unbind(object, "code", "title", "abstractThing", "goals", "draftMode");
		tuple.put("course", choices.getSelected().getKey());
		tuple.put("courses", choices);
		tuple.put("estimatedTime", estimatedTime);

		super.getResponse().setData(tuple);
	}
}
