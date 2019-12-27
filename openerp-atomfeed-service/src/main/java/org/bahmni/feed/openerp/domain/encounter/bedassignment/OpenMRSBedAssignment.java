package org.bahmni.feed.openerp.domain.encounter.bedassignment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bahmni.feed.openerp.ObjectMapperRepository;
import org.bahmni.feed.openerp.domain.OpenMRSPatient;
import org.bahmni.feed.openerp.domain.encounter.OpenERPOrder;
import org.bahmni.feed.openerp.domain.encounter.OpenERPOrders;
import org.bahmni.feed.openerp.domain.encounter.OpenMRSEncounterEvent;
import org.bahmni.openerp.web.OpenERPException;
import org.bahmni.openerp.web.request.builder.Parameter;
import org.bahmni.openerp.web.service.ProductService;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSBedAssignment extends OpenMRSEncounterEvent {
	private OpenMRSEncounter encounter;
	private OpenMRSPatient patient;
	private OpenMRSBed bed;
	private String uuid;
	private String startDatetime;
	private String endDatetime;
	
	private final String BED_TYPE_ORDER = "Bed Order";
	private final String ACTION_TYPE_NEW = "New";
	private final String ACTION_TYPE_REVISE = "Revise";
	
	@Override
	public String toString() {
		return "OpenMRSBedAssignment [encounter=" + encounter + ", patient=" + patient + ", bed=" + bed + "]";
	}

	public boolean shouldERPConsumeEvent() {
		return bed != null && patient != null && encounter != null;
	}
	
	public List<Parameter> getParameters(String eventId, ProductService productService, String feedURIForLastReadEntry, String feedURI)
			throws IOException {
		validateUrls(feedURIForLastReadEntry, feedURI);

		List<Parameter> parameters = new ArrayList<>();

		String patientId = patient.getDisplay().split(" ")[0];

		parameters.add(createParameter("category", "create.sale.order", "string"));
		parameters.add(createParameter("customer_id", patientId, "string"));
		parameters.add(createParameter("encounter_id", encounter.getUuid(), "string"));
		parameters.add(createParameter("feed_uri", feedURI, "string"));
		parameters.add(createParameter("last_read_entry_id", eventId, "string"));
		parameters.add(createParameter("feed_uri_for_last_read_entry", feedURIForLastReadEntry, "string"));
		parameters.add(createParameter("orders", getOrderJson(productService, bed, encounter), "string"));

		return parameters;
	}

	private String getOrderJson(ProductService productService, OpenMRSBed bed,
			OpenMRSEncounter encounter) throws IOException {
		String productId = productService.findProductByName(bed.getBedType().getName());
		
		if (productId == null)
			throw new OpenERPException("Product " + bed.getBedType().getName() + " not Found");

		OpenERPOrder openERPOrder = new OpenERPOrder();
//		openERPOrder.setVisitId(encounter.getVisit().getUuid());
		//        openERPOrder.setVisitType(encounter.getVisit().getVisitType());
		//        openERPOrder.setDescription(encounter.getVisit().getDescription());

		openERPOrder.setProductId(productId);
		openERPOrder.setProductName(bed.getBedType().getName());
		openERPOrder.setQuantity((double) 1);
		openERPOrder.setQuantityUnits("Day(s)");
		openERPOrder.setEncounterId(encounter.getUuid());
		openERPOrder.setVisitId(encounter.getVisit().getUuid());
		
		openERPOrder.setVisitType(encounter.getVisit().getVisitType().getName());
		openERPOrder.setType(BED_TYPE_ORDER);
		
		DateTime startDateTimeParsed = DateTime.parse(startDatetime);
		System.out.println("\n\n\n startDateTimeParsed-> \n\n\n"+startDateTimeParsed);
		
		long dayDiff = Days.daysBetween(startDateTimeParsed.toLocalDate(), DateTime.now().toLocalDate()).getDays();
		
		System.out.println("\n\n\n Day diff-> \n\n\n"+dayDiff);
		
		if (dayDiff < 1) {
			openERPOrder.setAction(ACTION_TYPE_NEW);
		} else {
			openERPOrder.setAction(ACTION_TYPE_REVISE);
//			openERPOrder.setPreviousOrderId(uuid);
		}
		
		//Generating random uuid, as the bed-management uuid is always same for a resource
		openERPOrder.setOrderId(UUID.randomUUID().toString());
		
        openERPOrder.setDispensed("false");

		OpenERPOrders orders = new OpenERPOrders(bed.getId());
		orders.getOpenERPOrders().add(openERPOrder);

		return ObjectMapperRepository.objectMapper.writeValueAsString(orders);
	}

	public OpenMRSPatient getPatient() {
		return patient;
	}

	public OpenMRSBed getBed() {
		return bed;
	}

	public OpenMRSEncounter getEncounter() {
		return encounter;
	}

	public String getUuid() {
		return uuid;
	}

	public String getStartDatetime() {
		return startDatetime;
	}

	public String getEndDatetime() {
		return endDatetime;
	}
	
}
