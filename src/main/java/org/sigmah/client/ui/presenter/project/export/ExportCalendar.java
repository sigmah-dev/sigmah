package org.sigmah.client.ui.presenter.project.export;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import javax.persistence.EntityManager;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.sigmah.server.dao.PersonalEventDAO;
import org.sigmah.server.domain.calendar.PersonalEvent;
import com.google.inject.persist.PersistService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletOutputStream;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Transp;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.model.property.XProperty;
import net.fortuna.ical4j.validate.ValidationException;
import org.hibernate.Session;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.util.EntityFilters;
import org.sigmah.server.inject.ConfigurationModule;
import org.sigmah.server.inject.I18nServerModule;
import org.sigmah.server.inject.MapperModule;
import org.sigmah.server.inject.PersistenceModule;
import org.sigmah.server.util.PersonnalEventLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class ExportCalendar
 */
public class ExportCalendar extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonnalEventLoader.class);
    /**
     * Application injector.
     */
    private Injector injector;

    @Override
    public void init(ServletConfig config) throws ServletException {
        // Do required initialization
        injector = Guice.createInjector(
                // Configuration module.
                new ConfigurationModule(),
                // Persistence module.
                new PersistenceModule(),
                // Mapper module.
                new MapperModule(),
                // I18nServer module.
                new I18nServerModule());
        injector.getInstance(PersistService.class).start();
        super.init(config);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        LOGGER.info("Export Sigmah Calendar is started by request from IP=" + getClientIpAddr(request));

        String paramIdName = "id";
        String paramIdValue = request.getParameter(paramIdName);
        String paramEventTypeName = "type";
        String paramEventTypeValue = request.getParameter(paramEventTypeName);

        if (paramIdValue != null
                && paramEventTypeValue != null) {
            if (paramEventTypeValue.equalsIgnoreCase("events")) {

//        final Injector injector = Guice.createInjector(
//                // Configuration module.
//                new ConfigurationModule(),
//                // Persistence module.
//                new PersistenceModule(),
//                // Mapper module.
//                new MapperModule(),
//                // I18nServer module.
//                new I18nServerModule());
                try {
                 //   injector.getInstance(PersistService.class).start();
                    final ProjectDAO projectDAO = injector.getInstance(ProjectDAO.class);
                    final Project project = projectDAO.findById(new Integer(paramIdValue));

                    final EntityManager em = injector.getProvider(EntityManager.class).get();
                    final PersonalEventDAO personalEventDAO = injector.getInstance(PersonalEventDAO.class);

                    Session session = em.unwrap(Session.class);
                    session.enableFilter(EntityFilters.HIDE_DELETED);

                    List<PersonalEvent> personalEventList = personalEventDAO.findAll();
                    String fileName = "Sigmah_" + project.getFullName() + "_calendar_" + paramEventTypeValue + ".ics";
                    net.fortuna.ical4j.model.Calendar sigmahICalendar = createICalForExport(personalEventList);

                    generateOutputToExportICal(response, fileName, sigmahICalendar);

                } catch (Exception ee) {
                    LOGGER.error("ExportCalendar " + ee.getMessage());
                    ee.printStackTrace();
                }

            } else {
                LOGGER.info("Export Sigmah Calendar is not implemented yet for parameter " + paramEventTypeName + " = " + paramEventTypeValue);
            }
        } else {
            LOGGER.info("Export Sigmah Calendar is not performed due to lack of parameters.");
        }
        LOGGER.info("Export Sigmah Calendar is finished.");
    }
/*
    private List<PersonalEvent> getEventListWithTypedQuery(final EntityManager em, final Project project) {
        //final EntityManager em = injector.getProvider(EntityManager.class).get();
        final TypedQuery<PersonalEvent> eventQuery
                = em.createQuery("SELECT p FROM PersonalEvent p "
                        + "WHERE p.calendarId = :calendarId and p.dateDeleted is null ORDER BY p.startDate", PersonalEvent.class);
        eventQuery.setParameter("calendarId", project.getCalendarId());
        
        List<PersonalEvent> events = eventQuery.getResultList();
        return events;
    }
*/
    private void generateOutputToExportICal(HttpServletResponse response, final String fileName, net.fortuna.ical4j.model.Calendar sigmahICalendar) throws ValidationException, IOException {
        response.setContentType("calendar/ical");
        response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        ServletOutputStream theServletOutputStream = response.getOutputStream();
        CalendarOutputter theCalendarOutputter = new CalendarOutputter();
        if (!sigmahICalendar.getComponents().isEmpty()) { // no calendar output if there is no component
            theCalendarOutputter.output(sigmahICalendar, theServletOutputStream);
            theServletOutputStream.flush();
        }
        if (theServletOutputStream != null) {
            try {
                theServletOutputStream.close();
            } catch (IOException ignore) {
            }
        }
    }
/*
    private void addNewPersonalEventToSigmahDB(final Project project, final Injector injector) {
        if (project != null) {
            int count =3;
            final EntityManager em = injector.getProvider(EntityManager.class).get();
            em.getTransaction().begin();
            LOGGER.info("Creating PersonalEvent for projectId=" + project.getId() );
            try {
                final java.util.Calendar cal = count < 365 ? initCalendar(2016) : initCalendar(2015);
                for (int i = 0; i < count; i++) {
                    cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
                    PersonalEvent personEvent = personalEventFactory(cal.getTime(), project.getCalendarId(), i);
                    em.merge(personEvent);
                }
                em.getTransaction().commit();
            } catch (Exception e) {
                LOGGER.error("An error occured while duplicating projects.", e);
                em.getTransaction().rollback();
            } finally {
                injector.getInstance(PersistService.class).stop();
            }
            LOGGER.info("PersonnalEventLoader ended with success");
        } else {
            LOGGER.error("Project  with id = " + project.getId() + " not found.");
            injector.getInstance(PersistService.class).stop();
            LOGGER.info("PersonnalEventLoader ended with errors");
        }
    }

    private PersonalEvent getPersonalEventById(final Injector injector, final String eventId) {
        PersonalEvent event = null;
        try {
            final PersonalEventDAO personalEventDAO = injector.getInstance(PersonalEventDAO.class);
            event = personalEventDAO.findById(new Integer(eventId));
        } catch (Exception ee) {
            LOGGER.error("getPersonalEventById " + ee.getMessage());
            ee.printStackTrace();
        }
        return event;
    }
    
     private static java.util.Calendar initCalendar(int year) {
        final java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.YEAR, year);
        cal.set(java.util.Calendar.MONTH, 0);
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        return cal;
    }    
*/
    /**
     * @param personalEventList
     * @return
     */
    private net.fortuna.ical4j.model.Calendar createICalForExport(
            List<PersonalEvent> personalEventList) {
        net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
        calendar.getProperties().add(new ProdId("-//Sigmah 2.2//iCal4j 2.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        calendar.getProperties().add(new XProperty("X-WR-CALNAME", "Sigmah"));
        calendar.getProperties().add(new XProperty("X-PUBLISHED-TTL", "PT1M"));
        calendar.getComponents().addAll(createICalEventsForExport(personalEventList));
        return calendar;
    }

    private List<VEvent> createICalEventsForExport(List<PersonalEvent> personalEventList) {
        List<VEvent> iCalEventList = new ArrayList<VEvent>();

        for (Iterator<PersonalEvent> iterator = personalEventList.iterator(); iterator.hasNext();) {
            PersonalEvent nextPersonalEvent = iterator.next();

            VEvent iCalEvent = new VEvent();
            iCalEvent.getProperties().add(new Uid(nextPersonalEvent.getId().toString()));
            iCalEvent.getProperties().add(new Summary(nextPersonalEvent.getSummary()));
            iCalEvent.getProperties().add(new Description(nextPersonalEvent.getDescription()));
            iCalEvent.getProperties().add(new DtStart(new DateTime(nextPersonalEvent.getStartDate().getTime())));
            iCalEvent.getProperties().add(new DtEnd(new DateTime(nextPersonalEvent.getEndDate().getTime())));
            iCalEvent.getProperties().add(new Created(new DateTime(nextPersonalEvent.getDateCreated())));
            iCalEvent.getProperties().add(new Transp("OPAQUE"));

            iCalEventList.add(iCalEvent);
        }
        return iCalEventList;
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }


    /**
     *
     * @param request
     * @return
     */
    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("Proxy-Client-IP");
        } else if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        } else if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        } else if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        } else if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        } else if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        } else if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_FORWARDED_FOR");
        } else if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_FORWARDED");
        } else if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_VIA");
        } else if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("REMOTE_ADDR");
        } else if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
