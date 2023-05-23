
package acme.features.lecturer.course;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.Course;
import acme.entities.Lecture;
import acme.entities.enums.CourseType;
import acme.framework.components.accounts.Principal;
import acme.framework.components.models.Tuple;
import acme.framework.services.AbstractService;
import acme.roles.Lecturer;

@Service
public class LecturerCourseListMineService extends AbstractService<Lecturer, Course> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected LecturerCourseRepository repository;

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
		Collection<Course> objects;
		Principal principal;

		principal = super.getRequest().getPrincipal();
		objects = this.repository.findManyCoursesByLecturerId(principal.getActiveRoleId());

		super.getBuffer().setData(objects);
	}

	@Override
	public void unbind(final Course object) {
		assert object != null;

		Collection<Lecture> lectures;

		lectures = this.repository.findManyLecturesByCourseId(object.getId());

		final CourseType courseType = object.courseType(lectures);

		Tuple tuple;
		String payload;

		tuple = super.unbind(object, "code", "title", "anAbstract");
		payload = String.format(//
			"%s; %s; %s", //
			object.getRetailPrice(), //
			object.getFurtherInformation(), //
			courseType);
		tuple.put("payload", payload);

		super.getResponse().setData(tuple);
	}

}
