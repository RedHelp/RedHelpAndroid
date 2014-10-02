package org.redhelp.data;

import java.io.Serializable;

/**
 * Created by harshis on 8/11/14.
 */
public class BloodProfileListData implements Serializable{
    public Long b_p_id;
    public String title;
    public String blood_grp;
    public byte[] profile_pic;
    public String distance;
    public Boolean isRequestAccepted;
}
