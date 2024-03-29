
package acme.features.student.course;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.entities.Course;
import acme.entities.Lecture;
import acme.framework.repositories.AbstractRepository;

@Repository
public interface StudentCourseRepository extends AbstractRepository {

	@Query("select c from Course c ")
	Collection<Course> findCourses( );

	@Query("select c from Course c where c.id = :id")
	Course findOneCourseById(int id);
	@Query("select c from CourseLecture c where c.course.id = :id")
	Collection<Lecture> findLecturesByCourse(int id);



}
