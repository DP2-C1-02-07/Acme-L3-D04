
package acme.features.authenticated.practicum;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.entities.Course;
import acme.entities.Practicum;
import acme.framework.repositories.AbstractRepository;

@Repository
public interface AuthenticatedPracticaRepository extends AbstractRepository {

	@Query("Select p From Practicum p Where p.course.id = :id")
	Collection<Practicum> findPracticaByCourseId(int id);

	@Query("Select p From Practicum p Where p.company.id = :id")
	Collection<Practicum> findPracticaByCompanyId(int id);

	@Query("Select p From Practicum p Where p.id = :id")
	Practicum findOnePracticaById(int id);

	@Query("Select c From Course c Where c.id = :id")
	Course findOneCourseById(int id);

	@Query("Select p.course From Practicum p Where p.id = :id")
	Course findOneCourseByPracticumId(int id);
}