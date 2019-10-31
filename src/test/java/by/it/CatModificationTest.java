package by.it;

import by.it.entity.CatModification;
import by.it.util.EmUtils;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.query.AuditEntity;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by yslabko on 10/10/2017.
 */
public class CatModificationTest {
    @Before
    public void init() {
        EntityManager em = EmUtils.getEntityManager();
        em.getTransaction().begin();
        CatModification cat = new CatModification(null, "Cat", "Tim", "brown", 2);
        em.persist(cat);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void updateTest() {
        EntityManager em = EmUtils.getEntityManager();
        em.getTransaction().begin();
        CatModification cat = em.find(CatModification.class, 1L);
        cat.setOwner("Lisa");
        em.flush();
        em.getTransaction().commit();
        em.getTransaction().begin();
        cat.setOwner("Tanya");
        em.flush();
        cat.setAge(3);
        em.flush();
        em.getTransaction().commit();
        System.out.println(AuditReaderFactory.get(em).getRevisions(CatModification.class, 1L));
        List<Object[]> catChangeList = AuditReaderFactory
                .get(em)
                .createQuery()
                .forRevisionsOfEntity(CatModification.class, false, true)
                .add(AuditEntity.id().eq(1L))
                .add(AuditEntity.property("owner").hasChanged())
                .getResultList();
        catChangeList.forEach(c -> {
            CatModification modCat = (CatModification) c[0];
            DefaultRevisionEntity entity = (DefaultRevisionEntity) c[1];
            int i = 0;
            System.out.println(++i + " " + modCat.getName() + " Owner:" + modCat.getOwner()
                    + " Revision: " + entity.getId()
                    + " Revision type: " + c[2]
                    + " updatedDate: " + entity.getRevisionDate());
        });

        em.close();
    }
}
