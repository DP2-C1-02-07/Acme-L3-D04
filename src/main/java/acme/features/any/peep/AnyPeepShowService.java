
package acme.features.any.peep;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.Peep;
import acme.framework.components.accounts.Any;
import acme.framework.components.models.Tuple;
import acme.framework.services.AbstractService;

@Service
public class AnyPeepShowService extends AbstractService<Any, Peep> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected AnyPeepRepository repository;

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
		int id;
		Peep peep;

		id = super.getRequest().getData("id", int.class);
		peep = this.repository.findOnePeepById(id);
		status = peep != null ;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Peep object;
		int id;

		id = super.getRequest().getData("id", int.class);
		object = this.repository.findOnePeepById(id);

		super.getBuffer().setData(object);

	}

	@Override
	public void unbind(final Peep object) {
		assert object != null;

		Tuple tuple;

		tuple = super.unbind(object, "title" , "instantiationMoment", "nick", "message" , "link", "email");

		super.getResponse().setData(tuple);
	}

}
