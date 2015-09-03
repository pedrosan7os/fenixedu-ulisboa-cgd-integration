package com.qubit.solution.fenixedu.integration.cgd.domain.idcards;

import java.util.Optional;

import org.fenixedu.academic.domain.Person;
import org.fenixedu.bennu.core.domain.Bennu;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.dml.DeletionListener;

public class CgdCard extends CgdCard_Base {

    static {
        FenixFramework.getDomainModel().registerDeletionListener(Person.class, new DeletionListener<Person>() {

            @Override
            public void deleting(Person person) {
                for (; !person.getCgdCardsSet().isEmpty(); person.getCgdCardsSet().iterator().next().delete());
            }
        });
    }

    protected CgdCard() {
        super();
        setRootDomainObject(Bennu.getInstance());
    }

    public CgdCard(Person person, String mifareCode, boolean temporary) {
        this();
        setPerson(person);
        setMifareCode(mifareCode);
        setIssueDate(new LocalDate());
        setTemporary(temporary);
    }

    @Override
    public void setMifareCode(String mifareCode) {
        super.setMifareCode(mifareCode);
        setLastMifareModication(new LocalDate());
    }

    @Atomic
    protected void delete() {
        setPerson(null);
        setRootDomainObject(null);
        super.deleteDomainObject();
    }

    public boolean isValid() {
        return getValidUntil() == null || getValidUntil().isAfter(new DateTime().toLocalDate());
    }

    public static Optional<CgdCard> findByPersonOptional(Person person) {
        return person.getCgdCardsSet().stream().filter(cgdCard -> cgdCard.isValid()).findFirst();
    }

    public static CgdCard findByPerson(Person person) {
        return findByPersonOptional(person).orElse(null);
    }

}
