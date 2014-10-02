package org.redhelp.data;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by harshis on 8/11/14.
 */
public class BloodProfileDataWrapper implements Serializable {
    public boolean showTitle;
    public Set<BloodProfileListData> bloodProfileListDataSortedSet;
}
