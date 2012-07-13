/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.

Oracle and Java are registered trademarks of Oracle and/or its affiliates.
Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package jsf2.demo.scrum.domain.sprint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import jsf2.demo.scrum.domain.project.Project;
import jsf2.demo.scrum.domain.story.Story;
import jsf2.demo.scrum.infra.entity.AbstractEntity;

/**
 * @author Dr. Spock (spock at dev.java.net)
 */
@Entity
@Table(name = "sprints", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "project_id"}))
@NamedQueries({@NamedQuery(name = "sprint.countByNameAndProject", query = "select count(s) from Sprint as s where s.name = :name and s.project = :project and not(s = :currentSprint)"),
        @NamedQuery(name = "sprint.new.countByNameAndProject", query = "select count(s) from Sprint as s where s.name = :name and s.project = :project")})
public class Sprint extends AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;
 
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="SPRINT_ID")
    @SequenceGenerator(name="SPRINT_ID")
    private Long id;
    
    @Override
    public Long getId() {
        return this.id;
    }
    
    @Column(nullable = false)
    @NotNull
    @Size(min=0, max=128)
    private String name;

    private String goals;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date", nullable = false)
    private Date startDate;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "iteration_scope")
    @Min(0)
    private int iterationScope;
    
    @Column(name = "gained_story_points")
    @Min(0)
    @Max(100)
    private int gainedStoryPoints;
    
    @Temporal(TemporalType.TIME)
    @Column(name = "daily_meeting_time")
    private Date dailyMeetingTime;
    
    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Story> stories = new ArrayList<Story>();
    
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public Sprint() {
        this.startDate = new Date();
    }

    public Sprint(String name) {
        this();
        this.name = name;
    }

    public Sprint(String name, Project project) {
        this(name);
        this.project = project;
    }

    @SprintNameUniquenessConstraint
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getIterationScope() {
        return iterationScope;
    }

    public void setIterationScope(int iterationScope) {
        this.iterationScope = iterationScope;
    }

    public int getGainedStoryPoints() {
        return gainedStoryPoints;
    }

    public void setGainedStoryPoints(int gainedStoryPoints) {
        this.gainedStoryPoints = gainedStoryPoints;
    }

    public Date getDailyMeetingTime() {
        return dailyMeetingTime;
    }

    public void setDailyMeetingTime(Date dailyMeetingTime) {
        this.dailyMeetingTime = dailyMeetingTime;
    }

    public List<Story> getStories() {
        return stories;
    }

    public boolean addStory(Story story) {
        if (story != null && !stories.contains(story)) {
            stories.add(story);
            story.setSprint(this);
            return true;
        }
        return false;
    }

    public boolean removeStory(Story story) {
        if (stories != null && !stories.isEmpty()) {
            return stories.remove(story);
        } else {
            return false;
        }
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Sprint other = (Sprint) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.project != other.project && (this.project == null || !this.project.equals(other.project))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.project != null ? this.project.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Sprint[name=" + name + ",startDate=" + startDate + ",project=" + project + "]";
    }
}