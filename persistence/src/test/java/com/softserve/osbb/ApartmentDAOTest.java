package com.softserve.osbb;

import com.softserve.osbb.dao.AppartamentDAO;
import com.softserve.osbb.model.ApartmentEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Oleg on 05.07.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OsbbApplicationRunner.class)
@Rollback
@Transactional
public class ApartmentDAOTest {
public static final Integer APPNUMBER = 111;
private ApartmentEntity apartmentEntity = new ApartmentEntity();
    @Autowired
    AppartamentDAO appartamentDAO;

    @Test
    public void testSave(){
        apartmentEntity.setNumber(APPNUMBER);
        appartamentDAO.save(apartmentEntity);
        Assert.assertNotNull(apartmentEntity);
        Assert.assertEquals(APPNUMBER, apartmentEntity.getNumber());
        appartamentDAO.delete(apartmentEntity.getApartmentId());
        Assert.assertFalse(appartamentDAO.exists(apartmentEntity.getApartmentId()));
    }

}
