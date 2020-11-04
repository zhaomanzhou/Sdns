package sdns.app.masterfile;

import java.util.List;
import java.util.NoSuchElementException;

import sdns.serialization.ResourceRecord;
import sdns.serialization.ValidationException;

public class MasterFileFactoryNoName {

	public static MasterFile makeMasterFile() throws Exception {
		return new MasterFile() {
			@Override
			public void search(String question, List<ResourceRecord> answers, List<ResourceRecord> nameservers,
					List<ResourceRecord> additionals)
					throws NoSuchElementException, NullPointerException, ValidationException {
			    throw new NoSuchElementException(question + " not found");
			}
		};
	}
}
