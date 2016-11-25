package com.qcadoo.mes.materialFlowResources.service;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.qcadoo.mes.materialFlowResources.constants.MaterialFlowResourcesConstants;
import com.qcadoo.mes.materialFlowResources.constants.ReservationFields;
import com.qcadoo.mes.materialFlowResources.constants.ResourceFields;
import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;

@Service
public class ResourceReservationsService {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void updateResourceQuantites(Map<String, Object> params, BigDecimal quantityToAdd) {
        if (params.get("resource_id") != null) {
            params.put("quantity_to_add", quantityToAdd);
            String query = "UPDATE materialflowresources_resource SET reservedquantity = reservedquantity + :quantity_to_add, "
                    + "availablequantity = availablequantity - :quantity_to_add WHERE id = :resource_id";
            jdbcTemplate.update(query, params);
        }
    }

    public void updateResourceQuantites(Entity reservation, BigDecimal quantityToAdd) {
        Entity resource = reservation.getBelongsToField(ReservationFields.RESOURCE);
        if (resource != null) {
            resource = dataDefinitionService
                    .get(MaterialFlowResourcesConstants.PLUGIN_IDENTIFIER, MaterialFlowResourcesConstants.MODEL_RESOURCE)
                    .get(resource.getId());
            BigDecimal reservedQuantity = resource.getDecimalField(ResourceFields.RESERVED_QUANTITY);
            // BigDecimal availableQuantity = resource.getDecimalField(ResourceFields.AVAILABLE_QUANTITY);
            // resource.setField(ResourceFields.AVAILABLE_QUANTITY, availableQuantity.subtract(quantityToAdd));
            resource.setField(ResourceFields.RESERVED_QUANTITY, reservedQuantity.add(quantityToAdd));
            resource.getDataDefinition().save(resource);
        }
    }
}
