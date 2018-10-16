/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices.filter;

import entity.Contact;
import entity.Customer;
import entity.Field;
import error.NoResultException;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import static javax.ws.rs.HttpMethod.POST;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import session.CustomerSessionLocal;

/**
 * REST Web Service
 *
 * @author victor
 */
//defines the path for the customer resources
@Path("customers")
public class CustomersResources {

    @EJB
    private CustomerSessionLocal customerSessionLocal;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Customer> getAlLCustomers() {
        return customerSessionLocal.searchCustomers(null);
    }

    @GET
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchCustomers(@QueryParam("name") String name, @QueryParam("phone") String phone,
            @QueryParam("email") String email, @QueryParam("field") String field, @QueryParam("value") String value) {
        if (name != null) {
            List<Customer> results
                    = customerSessionLocal.searchCustomers(name);
            GenericEntity<List<Customer>> entity = new GenericEntity<List<Customer>>(results) {
            };
            return Response.status(200).entity(
                    entity
            ).build();
        } else if (phone != null) {
            Contact c = new Contact();
            c.setPhone(phone);
            List<Customer> results
                    = customerSessionLocal.searchCustomersByContact(c);
            GenericEntity<List<Customer>> entity = new GenericEntity<List<Customer>>(results) {
            };
            return Response.status(200).entity(
                    entity
            ).build();
        } else if (email != null) {
            Contact c = new Contact();
            c.setEmail(email);
            List<Customer> results
                    = customerSessionLocal.searchCustomersByContact(c);
            GenericEntity<List<Customer>> entity = new GenericEntity<List<Customer>>(results) {
            };
            return Response.status(200).entity(
                    entity
            ).build();
        } else if (field != null && value != null) {
            Field f = new Field();
            f.setName(field);
            f.setFieldValue(value);
            List<Customer> results
                    = customerSessionLocal.searchCustomersByField(f);
            GenericEntity<List<Customer>> entity = new GenericEntity<List<Customer>>(results) {
            };
            return Response.status(200).entity(
                    entity
            ).build();
        } else {
            JsonObject exception = Json.createObjectBuilder()
                    .add("error", "No query conditions")
                    .build();
            return Response.status(400).entity(exception).build();
        }
    } //end searchCustomers

    @DELETE
    @Path("/{customer_id}/fields/{field_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteField(@PathParam("customer_id") Long cId,
            @PathParam("field_id") Long fId) {
        try {
            customerSessionLocal.deleteField(cId, fId);
            return Response.status(204).build();
        } catch (NoResultException e) {
            JsonObject exception = Json.createObjectBuilder()
                    .add("error", "Customer or field not found")
                    .build();
            return Response.status(404).entity(exception).build();
        }
    } //end deleteField

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Customer createCustomer(Customer c) {
        c.setCreated(new Date());
        customerSessionLocal.createCustomer(c);
        return c;
    } //end createCustomer
}
