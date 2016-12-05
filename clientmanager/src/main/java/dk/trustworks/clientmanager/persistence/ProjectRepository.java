package dk.trustworks.clientmanager.persistence;

import dk.trustworks.framework.model.Client;
import dk.trustworks.framework.model.Project;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by hans on 17/03/15.
 */
public class ProjectRepository {

    private static final Logger logger = LogManager.getLogger();
    private final Sql2o sql2o;

    public ProjectRepository(DataSource ds) {
        sql2o = new Sql2o(ds);
    }

    public List<Project> findAll() {
        try (Connection con = sql2o.open()) {
            List<Project> projects = con.createQuery("SELECT * FROM project ORDER BY name ASC").executeAndFetch(Project.class);
            con.close();
            return projects;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Project> findAllByClientUUIDs(List<Client> clients, boolean active) {
        String activeFilter = "";
        if(active) activeFilter = "AND active = 1";

        StringBuilder builder = new StringBuilder();
        for( int i = 0 ; i < clients.size(); i++ ) {
            builder.append("'"+clients.get(i).uuid+"',");
        }

        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM project WHERE clientuuid IN ("+builder.deleteCharAt( builder.length() -1 ).toString()+") "+activeFilter+" ORDER BY name ASC")
                    .executeAndFetch(Project.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Project findByUUID(String uuid) {
        try (Connection con = sql2o.open()) {
            Project project = con.createQuery("SELECT * FROM project WHERE uuid LIKE :uuid")
                    .addParameter("uuid", uuid)
                    .executeAndFetchFirst(Project.class);
            con.close();
            System.out.println("project = " + project);
            return project;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Project();
    }

    public List<Project> findByActiveTrue() {
        logger.debug("ProjectRepository.findByActiveTrueOrderByNameAsc");
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM project WHERE active = TRUE ORDER BY name ASC").executeAndFetch(Project.class);
        } catch (Exception e) {
            logger.error("LOG00360:", e);
        }
        return new ArrayList<>();
    }

    public List<Project> findByActiveFalse() {
        logger.debug("ProjectRepository.findByActiveFalseOrderByNameAsc");
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM project WHERE active = FALSE ORDER BY name ASC").executeAndFetch(Project.class);
        } catch (Exception e) {
            logger.error("LOG00870:", e);
        }
        return new ArrayList<>();
    }

    public List<Project> findByClientUUID(String clientUUID) {
        logger.debug("ProjectRepository.findByClientUUID");
        logger.debug("clientUUID = [" + clientUUID + "]");
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM project WHERE clientuuid LIKE :clientuuid ORDER BY name ASC").addParameter("clientuuid", clientUUID).executeAndFetch(Project.class);
        } catch (Exception e) {
            logger.error("LOG00370:", e);
        }
        return new ArrayList<>();
    }

    public List<Project> findByClientUUIDAndActiveTrue(String clientUUID) {
        logger.debug("ProjectRepository.findByClientUUIDAndActiveTrue");
        logger.debug("clientUUID = [" + clientUUID + "]");
        try (Connection con = sql2o.open()) {
            List<Project> projects = con.createQuery("SELECT * FROM project WHERE clientuuid LIKE :clientuuid AND active = true ORDER BY name ASC")
                    .addParameter("clientuuid", clientUUID)
                    .executeAndFetch(Project.class);
            con.close();
            System.out.println("projects.size() = " + projects.size());
            return projects;
        } catch (Exception e) {
            logger.error("LOG00380:", e);
        }
        return new ArrayList<>();
    }

    public void create(Project project) {
        logger.info("ProjectRepository.create");
        project.uuid = UUID.randomUUID().toString();
        project.active = true;
        project.created = DateTime.now();
        project.startdate = new Date(LocalDate.now().toDateTimeAtCurrentTime().getMillis());
        project.enddate = new Date(LocalDate.now().plusMonths(4).toDateTimeAtCurrentTime().getMillis());
        try (Connection con = sql2o.open()) {
            con.createQuery("INSERT INTO project (uuid, active, budget, clientuuid, created, customerreference, name, userowneruuid, clientdatauuid, startdate, enddate) VALUES (:uuid, :active, :budget, :clientuuid, :created, :customerreference, :name, :userowneruuid, :clientdatauuid, :startdate, :enddate)")
                    .bind(project)
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00410:", e);
        }
    }

    public void update(Project project, String uuid) {
        logger.info("ProjectRepository.update");
        project.uuid = uuid;
        try (Connection con = sql2o.open()) {
            con.createQuery("UPDATE project p SET p.active = :active, p.budget = :budget, p.customerreference = :customerreference, p.name = :name, p.userowneruuid = :userowneruuid, p.startdate = :startdate, p.enddate = :enddate WHERE p.uuid LIKE :uuid")
                    .bind(project)
                    .executeUpdate();
        } catch (Exception e) {
            logger.error("LOG00420:", e);
        }
    }
}
