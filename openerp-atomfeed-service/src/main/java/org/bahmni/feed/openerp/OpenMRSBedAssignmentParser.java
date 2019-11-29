/*
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/ 
* 
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
* 
* The Original Code is OpenELIS code.
* 
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*/

package org.bahmni.feed.openerp;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;
import org.bahmni.feed.openerp.domain.encounter.bedassignment.OpenMRSBedAssignment;
import org.bahmni.openerp.web.request.OpenERPRequest;
import org.bahmni.openerp.web.request.builder.Parameter;
import org.bahmni.openerp.web.service.ProductService;

import java.io.IOException;
import java.util.List;

public class OpenMRSBedAssignmentParser implements WebClientResponseParser {
    private ObjectMapper objectMapper;

    public OpenMRSBedAssignmentParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    private static Logger logger = Logger.getLogger(OpenMRSBedAssignmentParser.class);
	

    @Override
    public OpenERPRequest parse(String responseContent, ProductService productService, String eventId, String feedURIForLastReadEntry, String feedURI) throws IOException {
    	logger.error("\n\n\n\n\nInside parse \n\n\n\n\n");
    	logger.error("\n\n\n\n\nresponseContent=>"+responseContent);
    	OpenMRSBedAssignment openMRSBedAssignment = objectMapper.readValue(responseContent, OpenMRSBedAssignment.class);
        
    	logger.error("\n\n\n\nopenMRSBedAssignment=>"+openMRSBedAssignment);
        
        if (!openMRSBedAssignment.shouldERPConsumeEvent()) {
            return OpenERPRequest.DO_NOT_CONSUME;
        }

        List<Parameter> parameters = openMRSBedAssignment.getParameters(eventId, productService, feedURIForLastReadEntry, feedURI);
        return new OpenERPRequest("atom.event.worker", "process_event", parameters);

    }
}
