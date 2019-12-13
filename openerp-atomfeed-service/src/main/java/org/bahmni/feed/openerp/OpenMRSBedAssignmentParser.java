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

import java.io.IOException;
import java.util.List;

import org.bahmni.feed.openerp.domain.encounter.bedassignment.OpenMRSBedAssignment;
import org.bahmni.openerp.web.request.OpenERPRequest;
import org.bahmni.openerp.web.request.builder.Parameter;
import org.bahmni.openerp.web.service.ProductService;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OpenMRSBedAssignmentParser implements WebClientResponseParser {
    private ObjectMapper objectMapper;

    public OpenMRSBedAssignmentParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    

    @Override
    public OpenERPRequest parse(String responseContent, ProductService productService, String eventId, String feedURIForLastReadEntry, String feedURI) throws IOException {
    	OpenMRSBedAssignment openMRSBedAssignment = objectMapper.readValue(responseContent, OpenMRSBedAssignment.class);
        
        if (!openMRSBedAssignment.shouldERPConsumeEvent()) {
            return OpenERPRequest.DO_NOT_CONSUME;
        }

        List<Parameter> parameters = openMRSBedAssignment.getParameters(eventId, productService, feedURIForLastReadEntry, feedURI);
        return new OpenERPRequest("atom.event.worker", "process_event", parameters);

    }
}
