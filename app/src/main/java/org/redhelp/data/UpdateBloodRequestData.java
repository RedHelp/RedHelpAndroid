package org.redhelp.data;

import org.redhelp.common.types.UpdateBloodRequestState;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by harshis on 9/23/14.
 */
public class UpdateBloodRequestData implements Serializable {

    public Long b_r_id;
    public UpdateBloodRequestState state;
    public HashMap<Long, String> donorsHashMap;

}
