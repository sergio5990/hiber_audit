package by.it;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReaderFactory;
import org.junit.Before;
import org.junit.Test;

import by.it.entity.Cat;
import by.it.util.EmUtils;

/**
 * Class LockTest
 * <p>
 * Created by yslabko on 10/06/2017.
 */
public class CatTest {
    @Before
    public void init() {
        EntityManager em = EmUtils.getEntityManager();
        em.getTransaction().begin();
        Cat cat = new Cat(null, "Cat", "Tim", "brown", 2, null);
        em.persist(cat);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void updateTest() {
        EntityManager em = EmUtils.getEntityManager();
        em.getTransaction().begin();
        Cat cat = em.find(Cat.class, 1L);
        cat.setOwner("Lisa");
        em.flush();
        em.getTransaction().commit();
        em.getTransaction().begin();
        cat.setOwner("Tanya");
        em.flush();
        cat.setAge(3);
        em.flush();
        em.getTransaction().commit();
        List<Number> revisions = AuditReaderFactory.get(em).getRevisions(Cat.class, 1L);
        System.out.println(revisions);
        for (Number revision : revisions) {
            System.out.println(AuditReaderFactory.get(em)
                    .createQuery()
                    .forEntitiesAtRevision(Cat.class, revision).getSingleResult());
        }

        em.close();
    }
}
