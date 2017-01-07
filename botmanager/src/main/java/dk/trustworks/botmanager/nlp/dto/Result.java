package dk.trustworks.botmanager.nlp.dto;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 02/06/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "source",
        "resolvedQuery",
        "action",
        "actionIncomplete",
        "parameters",
        "contexts",
        "metadata",
        "fulfillment",
        "score"
})
public class Result {

    @JsonProperty("source")
    private String source;
    @JsonProperty("resolvedQuery")
    private String resolvedQuery;
    @JsonProperty("action")
    private String action;
    @JsonProperty("actionIncomplete")
    private boolean actionIncomplete;
    @JsonProperty("parameters")
    private Parameters parameters;
    @JsonProperty("contexts")
    private List<Object> contexts = new ArrayList<Object>();
    @JsonProperty("metadata")
    private Metadata metadata;
    @JsonProperty("fulfillment")
    private Fulfillment fulfillment;
    @JsonProperty("score")
    private double score;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     *
     * @return
     * The source
     */
    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    /**
     *
     * @param source
     * The source
     */
    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    /**
     *
     * @return
     * The resolvedQuery
     */
    @JsonProperty("resolvedQuery")
    public String getResolvedQuery() {
        return resolvedQuery;
    }

    /**
     *
     * @param resolvedQuery
     * The resolvedQuery
     */
    @JsonProperty("resolvedQuery")
    public void setResolvedQuery(String resolvedQuery) {
        this.resolvedQuery = resolvedQuery;
    }

    /**
     *
     * @return
     * The action
     */
    @JsonProperty("action")
    public String getAction() {
        return action;
    }

    /**
     *
     * @param action
     * The action
     */
    @JsonProperty("action")
    public void setAction(String action) {
        this.action = action;
    }

    /**
     *
     * @return
     * The actionIncomplete
     */
    @JsonProperty("actionIncomplete")
    public boolean isActionIncomplete() {
        return actionIncomplete;
    }

    /**
     *
     * @param actionIncomplete
     * The actionIncomplete
     */
    @JsonProperty("actionIncomplete")
    public void setActionIncomplete(boolean actionIncomplete) {
        this.actionIncomplete = actionIncomplete;
    }

    /**
     *
     * @return
     * The parameters
     */
    @JsonProperty("parameters")
    public Parameters getParameters() {
        return parameters;
    }

    /**
     *
     * @param parameters
     * The parameters
     */
    @JsonProperty("parameters")
    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    /**
     *
     * @return
     * The contexts
     */
    @JsonProperty("contexts")
    public List<Object> getContexts() {
        return contexts;
    }

    /**
     *
     * @param contexts
     * The contexts
     */
    @JsonProperty("contexts")
    public void setContexts(List<Object> contexts) {
        this.contexts = contexts;
    }

    /**
     *
     * @return
     * The metadata
     */
    @JsonProperty("metadata")
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    @JsonProperty("metadata")
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The fulfillment
     */
    @JsonProperty("fulfillment")
    public Fulfillment getFulfillment() {
        return fulfillment;
    }

    /**
     *
     * @param fulfillment
     * The fulfillment
     */
    @JsonProperty("fulfillment")
    public void setFulfillment(Fulfillment fulfillment) {
        this.fulfillment = fulfillment;
    }

    /**
     *
     * @return
     * The score
     */
    @JsonProperty("score")
    public double getScore() {
        return score;
    }

    /**
     *
     * @param score
     * The score
     */
    @JsonProperty("score")
    public void setScore(double score) {
        this.score = score;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Result{");
        sb.append("source='").append(source).append('\'');
        sb.append(", resolvedQuery='").append(resolvedQuery).append('\'');
        sb.append(", action='").append(action).append('\'');
        sb.append(", actionIncomplete=").append(actionIncomplete);
        sb.append(", parameters=").append(parameters);
        sb.append(", contexts=").append(contexts);
        sb.append(", metadata=").append(metadata);
        sb.append(", fulfillment=").append(fulfillment);
        sb.append(", score=").append(score);
        sb.append(", additionalProperties=").append(additionalProperties);
        sb.append('}');
        return sb.toString();
    }
}
