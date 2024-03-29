
package acme.features.assistant.tutorialSession;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.Tutorial;
import acme.entities.TutorialSession;
import acme.entities.enums.TutorialSessionType;
import acme.framework.components.jsp.SelectChoices;
import acme.framework.components.models.Tuple;
import acme.framework.helpers.MomentHelper;
import acme.framework.services.AbstractService;
import acme.roles.Assistant;

@Service
public class AssistantTutorialSessionDeleteService extends AbstractService<Assistant, TutorialSession> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AssistantTutorialSessionRepository repository;

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
		int tutorialSessionId;
		TutorialSession tutorialSession;
		Tutorial tutorial;
		Assistant assistant;

		tutorialSessionId = super.getRequest().getData("id", int.class);
		tutorialSession = this.repository.findOneTutorialSessionById(tutorialSessionId);
		tutorial = tutorialSession == null ? null : tutorialSession.getTutorial();
		assistant = tutorial == null ? null : tutorial.getAssistant();
		status = tutorialSession != null && tutorial != null && tutorial.isDraftMode() && tutorial.getAssistant().getId() == super.getRequest().getPrincipal().getActiveRoleId() //
			&& super.getRequest().getPrincipal().hasRole(assistant);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		TutorialSession object;
		int tutorialSessionId;

		tutorialSessionId = super.getRequest().getData("id", int.class);
		object = this.repository.findOneTutorialSessionById(tutorialSessionId);

		super.getBuffer().setData(object);
	}

	@Override
	public void bind(final TutorialSession object) {
		assert object != null;

		super.bind(object, "title", "abstractSession", "sessionType", "startDate", "finishDate", "info");

	}

	@Override
	public void validate(final TutorialSession tutorialSession) {
		assert tutorialSession != null;
	}

	@Override
	public void perform(final TutorialSession object) {
		assert object != null;

		Tutorial tutorial;
		double totalHours;
		double sessionHours;
		double validFormatSessionHours;
		Duration sessionDuration;
		String formattedSessionHours;

		tutorial = object.getTutorial();
		totalHours = 0.;
		sessionDuration = MomentHelper.computeDuration(object.getStartDate(), object.getFinishDate());
		sessionHours = sessionDuration.getSeconds() / 3600.;
		formattedSessionHours = String.format("%.2f", sessionHours);
		validFormatSessionHours = Double.parseDouble(formattedSessionHours);

		totalHours = tutorial.getEstimatedTotalTime() - validFormatSessionHours;

		tutorial.setEstimatedTotalTime(totalHours);

		this.repository.save(tutorial);
		this.repository.delete(object);
	}

	@Override
	public void unbind(final TutorialSession object) {
		assert object != null;

		Tuple tuple;
		SelectChoices choices;
		Tutorial tutorial;

		choices = SelectChoices.from(TutorialSessionType.class, object.getSessionType());
		tutorial = object.getTutorial();

		tuple = super.unbind(object, "title", "abstractSession", "sessionType", "startDate", "finishDate", "info");
		tuple.put("masterId", tutorial.getId());
		tuple.put("draftMode", tutorial.isDraftMode());
		tuple.put("sessionType", choices);

		super.getResponse().setData(tuple);
	}

}
