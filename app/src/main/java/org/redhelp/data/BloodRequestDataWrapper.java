package org.redhelp.data;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by harshis on 9/1/14.
 */
public class BloodRequestDataWrapper implements Serializable {
    public Set<BloodRequestListData> bloodRequestListDataList;
    public boolean showTitle;
}
