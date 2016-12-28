/**
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: paulo.abrantes@qub-it.com
 *
 * 
 * This file is part of FenixEdu fenixedu-ulisboa-cgdIntegration.
 *
 * FenixEdu fenixedu-ulisboa-cgdIntegration is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu fenixedu-ulisboa-cgdIntegration is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu fenixedu-ulisboa-cgdIntegration.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.qubit.solution.fenixedu.integration.cgd.webservices.messages;

import org.apache.commons.lang.StringUtils;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.student.Student;
import org.fenixedu.bennu.core.domain.User;

import com.qubit.solution.fenixedu.integration.cgd.domain.configuration.CgdIntegrationConfiguration;
import com.qubit.solution.fenixedu.integration.cgd.webservices.resolver.memberid.IMemberIDAdapter;
import org.fenixedu.bennu.core.groups.Group;

public class CgdMessageUtils {

    public static int REPLY_CODE_OPERATION_OK = 0;
    public static int REPLY_CODE_INFORMATION_NOT_OK = 1;
    public static int REPLY_CODE_UNEXISTING_MEMBER = 9;

    public static Person readPersonByMemberCode(String populationCode, String memberCode) {
        Person requestedPerson = null;
        if (!StringUtils.isEmpty(memberCode) && !StringUtils.isEmpty(populationCode)) {
            if (populationCode.charAt(0) == 'A') {
                if (StringUtils.isNumeric(memberCode)) {
                    Student student = Student.readStudentByNumber(Integer.valueOf(memberCode));
                    if (student != null) {
                        requestedPerson = student.getPerson();
                    }
                }
            }
            if (requestedPerson == null) {
                final User user = User.findByUsername(memberCode);
                if (user != null) {
                    requestedPerson = user.getPerson();
                }
            }
        }
        return requestedPerson;
    }

    public static boolean verifyMatch(Person person, String populationCode, String memberCode, String memberID) {
        final String mid = getMemberIDStrategy().retrieveMemberID(person);
        if (!StringUtils.isEmpty(populationCode) && !StringUtils.isEmpty(memberCode)) {
            final char pc = populationCode.charAt(0);
            if (pc == 'A' && person.getStudent() == null) {
                return false;
            }
            if (pc == 'F' && !Group.dynamic("employees").isMember(person.getUser())) {
                return false;
            }
            if (pc == 'D' && person.getTeacher() == null) {
                return false;
            }
            if (!memberCode.equals(mid)) {
                return false;
            }
        }
        return !StringUtils.isEmpty(memberID) && memberID.equals(mid);
    }

    public static IMemberIDAdapter getMemberIDStrategy() {
        return CgdIntegrationConfiguration.getInstance().getMemberIDStrategy();
    }
}
