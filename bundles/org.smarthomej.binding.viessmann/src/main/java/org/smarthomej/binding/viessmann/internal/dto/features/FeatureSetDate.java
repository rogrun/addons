
package org.smarthomej.binding.viessmann.internal.dto.features;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The {@link FeatureSetDate} is responsible for
 *
 * @author Ronny Grun - Initial contribution
 */
public class FeatureSetDate {

    @SerializedName("required")
    @Expose
    public Boolean required;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("constraints")
    @Expose
    public FeatureConstraintsEmpty constraints;
}
