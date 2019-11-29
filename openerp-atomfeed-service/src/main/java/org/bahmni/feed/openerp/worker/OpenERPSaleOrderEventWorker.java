package org.bahmni.feed.openerp.worker;

import java.io.IOException;
import java.net.URI;

import org.apache.log4j.Logger;
import org.bahmni.feed.openerp.ObjectMapperRepository;
import org.bahmni.feed.openerp.OpenMRSBedAssignmentParser;
import org.bahmni.feed.openerp.client.OpenMRSWebClient;
import org.bahmni.feed.openerp.domain.encounter.MapERPOrders;
import org.bahmni.feed.openerp.domain.encounter.OpenMRSEncounter;
import org.bahmni.feed.openerp.domain.visit.OpenMRSVisit;
import org.bahmni.feed.openerp.utils.ApplicationContextProvider;
import org.bahmni.openerp.web.client.OpenERPClient;
import org.bahmni.openerp.web.request.OpenERPRequest;
import org.bahmni.openerp.web.request.builder.Parameter;
import org.bahmni.openerp.web.service.ProductService;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.service.EventWorker;

public class OpenERPSaleOrderEventWorker implements EventWorker {
    OpenERPClient openERPClient;
    private String feedUrl;
    private OpenMRSWebClient webClient;
    private String urlPrefix;
    private ProductService productService;
    public static final String BED_EVENT_TITLE = "Bed-Assignment";

    private static Logger logger = Logger.getLogger(OpenERPSaleOrderEventWorker.class);

    public OpenERPSaleOrderEventWorker(String feedUrl, OpenERPClient openERPClient, OpenMRSWebClient webClient, String urlPrefix) {
        this.feedUrl = feedUrl;
        this.openERPClient = openERPClient;
        this.webClient = webClient;
        this.urlPrefix = urlPrefix;
        this.productService = ApplicationContextProvider.getApplicationContext().getBean(ProductService.class);
    }

    @Override
    public void process(Event event) {
    	System.out.println("Inside process event of sales order \n\n\n\n\n");
    	System.out.println("event titke=>"+event.getTitle());
    	
        try {
        	OpenERPRequest openERPRequest = BED_EVENT_TITLE.equals(event.getTitle()) ? mapBedAssignmentRequest(event) : mapRequest(event);
            if (!openERPRequest.shouldERPConsumeEvent())
                return;

            openERPClient.execute(openERPRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cleanUp(Event event) {
    }
    
    private OpenERPRequest mapBedAssignmentRequest(Event event) throws IOException {
    	logger.error("\n\n\n\n\nInside mapBedAssignmentRequest \n\n\n\n\n");
        String encounterEventContent = webClient.get(URI.create(urlPrefix + event.getContent()));
        OpenMRSBedAssignmentParser openMRSBedAssignmentParser = new OpenMRSBedAssignmentParser(ObjectMapperRepository.objectMapper);
        
        OpenERPRequest erpRequest = openMRSBedAssignmentParser.parse(encounterEventContent, productService, event.getId(), event.getFeedUri(), feedUrl);
        logger.error("\n\n erpRequest =>"+erpRequest);
        if (event.getFeedUri() == null)
            erpRequest.addParameter(createParameter("is_failed_event", "1", "boolean"));

        return erpRequest;
    }

    private OpenERPRequest mapRequest(Event event) throws IOException {

        String encounterEventContent = webClient.get(URI.create(urlPrefix + event.getContent()));
        OpenMRSEncounter openMRSEncounter = ObjectMapperRepository.objectMapper.readValue(encounterEventContent, OpenMRSEncounter.class);

        // Ignore Bed Assignment Encounter events
        if(!openMRSEncounter.shouldERPConsumeEvent())
            return OpenERPRequest.DO_NOT_CONSUME;

        String visitURL = "/openmrs/ws/rest/v1/visit/" + openMRSEncounter.getVisitUuid() + "?v=full";
        String visitContent = webClient.get(URI.create(urlPrefix + visitURL));

        OpenMRSVisit openMRSVisit = ObjectMapperRepository.objectMapper.readValue(visitContent, OpenMRSVisit.class);
        MapERPOrders mapERPOrders = new MapERPOrders(openMRSEncounter, openMRSVisit);

        OpenERPRequest erpRequest = new OpenERPRequest("atom.event.worker", "process_event", mapERPOrders.getParameters(event.getId(), event.getFeedUri(), feedUrl));
        if (event.getFeedUri() == null)
            erpRequest.addParameter(createParameter("is_failed_event", "1", "boolean"));

        return erpRequest;
    }

    private Parameter createParameter(String name, String value, String type) {
        return new Parameter(name, value, type);
    }
}
