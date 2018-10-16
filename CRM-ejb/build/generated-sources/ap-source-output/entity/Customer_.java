package entity;

import entity.Contact;
import entity.Field;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2018-10-09T01:39:34")
@StaticMetamodel(Customer.class)
public class Customer_ { 

    public static volatile SingularAttribute<Customer, Byte> gender;
    public static volatile SingularAttribute<Customer, Date> dob;
    public static volatile SingularAttribute<Customer, Date> created;
    public static volatile SingularAttribute<Customer, String> name;
    public static volatile SingularAttribute<Customer, Long> id;
    public static volatile ListAttribute<Customer, Field> fields;
    public static volatile ListAttribute<Customer, Contact> contacts;

}