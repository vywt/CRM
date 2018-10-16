package managedbean;

import entity.Contact;
import entity.Customer;
import entity.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import session.CustomerSessionLocal;

@ManagedBean
@ViewScoped
public class CustomerManagedBean {

    @EJB
    CustomerSessionLocal customerSessionLocal;

    private String name;
    private byte gender;
    private Date dob;
    private List<Customer> customers;

    //used by editCustomer.xhtml
    private Long cId;
    private Customer selectedCustomer;

    private String fieldName;
    private String fieldValue;
    private String contactType = "PHONE";
    private String contactValue;

    private String searchType = "NAME";
    private String searchString;
    private Set<String> allFields;

    public CustomerManagedBean() {
    }

    @PostConstruct
    public void init() {
        if (searchString == null || searchString.equals("")) {
            customers = customerSessionLocal.searchCustomers(null);
        } else {
            switch (searchType) {
                case "NAME":
                    customers = customerSessionLocal.searchCustomers(searchString);
                    break;
                case "PHONE": {
                    Contact c = new Contact();
                    c.setPhone(searchString);
                    customers = customerSessionLocal.searchCustomersByContact(c);
                    break;
                }
                case "EMAIL": {
                    Contact c = new Contact();
                    c.setEmail(searchString);
                    customers = customerSessionLocal.searchCustomersByContact(c);
                    break;
                }
                default:
                    Field f = new Field();
                    f.setName(searchType);
                    f.setFieldValue(searchString);
                    customers = customerSessionLocal.searchCustomersByField(f);
                    break;
            }
        }

        allFields = customerSessionLocal.getAllFieldNames();
    }

    public void searchTypeHandler() {
        //do nothing, just to force an explicit update of searchType
    } //end searchTypeHandler

    public void loadSelectedCustomer() {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            this.selectedCustomer = customerSessionLocal.getCustomer(cId);
            name = this.selectedCustomer.getName();
            gender = this.selectedCustomer.getGender();
            dob = this.selectedCustomer.getDob();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to load customer"));
        }
    } //end loadSelectedCustomer

    public void handleSearch() {
        init();
    }

    public void addCustomer(ActionEvent evt) {
        Customer c = new Customer();
        c.setName(name);
        c.setGender(gender);
        c.setDob(dob);
        c.setCreated(new Date());

        customerSessionLocal.createCustomer(c);
    } //end addCustomer

    public void updateCustomer(ActionEvent evt) {
        FacesContext context = FacesContext.getCurrentInstance();
        selectedCustomer.setName(name);
        selectedCustomer.setGender(gender);
        selectedCustomer.setDob(dob);

        try {
            customerSessionLocal.updateCustomer(selectedCustomer);
        } catch (Exception e) {
            //show with an error icon
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to update customer"));
            return;
        }

        //need to make sure reinitialize the customers collection
        context.addMessage(null, new FacesMessage("Success", "Successfully updated customer"));
    } //end updateCustomer

    public void deleteCustomer() {
        FacesContext context = FacesContext.getCurrentInstance();

        Map<String, String> params = context.getExternalContext()
                .getRequestParameterMap();
        String cIdStr = params.get("cId");
        Long cId = Long.parseLong(cIdStr);

        try {
            customerSessionLocal.deleteCustomer(cId);
        } catch (Exception e) {
            //show with an error icon
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to delete customer"));
            return;
        }

        context.addMessage(null, new FacesMessage("Success", "Successfully deleted customer"));
        init();

    } //end deleteCustomer

    public void deleteField(Long cId, Long fId) {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            customerSessionLocal.deleteField(cId, fId);
            this.selectedCustomer = customerSessionLocal.getCustomer(cId);
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to delete field"));
        }
    }

    public void deleteContact(Long cId) {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            customerSessionLocal.deleteContact(cId);
            this.selectedCustomer = customerSessionLocal.getCustomer(this.selectedCustomer.getId());
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to delete contact"));
        }
    }

    public void addField() {
        FacesContext context = FacesContext.getCurrentInstance();
        Field f = new Field();
        f.setName(this.fieldName);
        f.setFieldValue(this.fieldValue);

        try {
            customerSessionLocal.addField(this.selectedCustomer.getId(), f);
            this.selectedCustomer = customerSessionLocal.getCustomer(this.selectedCustomer.getId());

            //reset values
            this.fieldName = "";
            this.fieldValue = "";
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to add field"));
        }
    }

    public void addContact() {
        FacesContext context = FacesContext.getCurrentInstance();

        Contact c = new Contact();

        if (this.contactType.equals("PHONE")) {
            c.setPhone(this.contactValue);
        } else if (this.contactType.equals("EMAIL")) {
            c.setEmail(this.contactValue);
        }

        try {
            customerSessionLocal.addContact(this.selectedCustomer.getId(), c);
            this.selectedCustomer = customerSessionLocal.getCustomer(this.selectedCustomer.getId());

            //reset values
            this.contactType = "PHONE";
            this.contactValue = "";
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Unable to add contact"));
        }
    } //end addContact

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the gender
     */
    public byte getGender() {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(byte gender) {
        this.gender = gender;
    }

    /**
     * @return the dob
     */
    public Date getDob() {
        return dob;
    }

    /**
     * @param dob the dob to set
     */
    public void setDob(Date dob) {
        this.dob = dob;
    }

    /**
     * @return the customers
     */
    public List<Customer> getCustomers() {
        return customers;
    }

    /**
     * @param customers the customers to set
     */
    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    /**
     * @return the cId
     */
    public Long getcId() {
        return cId;
    }

    /**
     * @param cId the cId to set
     */
    public void setcId(Long cId) {
        this.cId = cId;
    }

    /**
     * @return the selectedCustomer
     */
    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    /**
     * @param selectedCustomer the selectedCustomer to set
     */
    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the fieldValue
     */
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * @param fieldValue the fieldValue to set
     */
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    /**
     * @return the contactType
     */
    public String getContactType() {
        return contactType;
    }

    /**
     * @param contactType the contactType to set
     */
    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    /**
     * @return the contactValue
     */
    public String getContactValue() {
        return contactValue;
    }

    /**
     * @param contactValue the contactValue to set
     */
    public void setContactValue(String contactValue) {
        this.contactValue = contactValue;
    }

    /**
     * @return the searchType
     */
    public String getSearchType() {
        return searchType;
    }

    /**
     * @param searchType the searchType to set
     */
    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    /**
     * @return the searchString
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * @param searchString the searchString to set
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    /**
     * @return the allFields
     */
    public Set<String> getAllFields() {
        return allFields;
    }

    /**
     * @param allFields the allFields to set
     */
    public void setAllFields(Set<String> allFields) {
        this.allFields = allFields;
    }
}
