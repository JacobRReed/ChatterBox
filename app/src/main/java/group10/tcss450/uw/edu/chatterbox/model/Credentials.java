package group10.tcss450.uw.edu.chatterbox.model;

import android.text.Editable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Class to encapsulate credentials fields. Building an Object requires a username and password.
 *
 * Optional fields include email, first and last name.
 *
 * Password field is never stored as a String object. The method getPassword allows only one access
 * and clears the password field after the initial access.
 *
 * Accessing the fields using the asJSONObject method does not clear the password field. Repeated
 * calls to asJSONObject continue to include the password. However, calls to asJSONObject after
 * getPassword has been used will result in an empty password in the resulting JSON object.
 *
 * @author Charles Bryan
 * @version 14 April 2018
 */
public class Credentials implements Serializable {
    private static final long serialVersionUID = -1634677417576883013L;

    private final String mUsername;
    private final Editable mPassword;

    private String mFirstName;
    private String mLastName;
    private String mEmail;

    /**
     * Helper class for building Credentials.
     *
     * @author Charles Bryan
     */
    public static class Builder {
        private final String mUsername;
        private final Editable mPassword;

        private String mFirstName = "";
        private String mLastName = "";
        private String mEmail = "";

        /**
         * Constructs a new Builder.
         *
         * Password field is never stored as a String object.
         *
         * @param username the username
         * @param password the password
         */
        public Builder(String username, Editable password) {
            mUsername = username;
            mPassword = password;
        }


        /**
         * Add an optional first name.
         * @param val an optional first name
         * @return
         */
        public Builder addFirstName(final String val) {
            mFirstName = val;
            return this;
        }

        /**
         * Add an optional last name.
         * @param val an optional last name
         * @return
         */
        public Builder addLastName(final String val) {
            mLastName = val;
            return this;
        }

        /**
         * Add an optional email. No validation is performed. Ensure that the argument is a
         * valid email before adding here if you wish to perform validation.
         * @param val an optional email
         * @return
         */
        public Builder addEmail(final String val) {
            mEmail = val;
            return this;
        }

        public Credentials build() {
            return new Credentials(this);
        }
    }

    /**
     * Construct a Credentials internally from a builder.
     *
     * @param builder the builder used to construct this object
     */
    private Credentials(final Builder builder) {
        mUsername = builder.mUsername;
        mPassword = builder.mPassword;
        mFirstName = builder.mFirstName;
        mLastName = builder.mLastName;
        mEmail = builder.mEmail;
    }

    /**
     * Get the Username.
     * @return the username
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Get the password. This method may be called only once. After the first occurance,
     * the password is cleared from memory and an IllegalStateException is thrown on further
     * calls.
     * @return the password
     * @throws IllegalStateException client code may only access the password once.
     */
    public Editable getPassword() {
        if (mPassword.length() == 0) {
            throw new IllegalStateException("This password may only be accessed once.");
        }
        Editable temp = Editable.Factory.getInstance().newEditable(mPassword);
        mPassword.clear();
        return temp;
    }

    /**
     * Get the first name or the empty string if no first name was provided.
     * @return the first name or the empty string if no first name was provided.
     */
    public String getFirstName() {
        return mFirstName;
    }

    /**
     * Get the last name or the empty string if no first name was provided.
     * @return the last name or the empty string if no first name was provided.
     */
    public String getLastName() {
        return mLastName;
    }

    /**
     * Get the email or the empty string if no first name was provided.
     * @return the email or the empty string if no first name was provided.
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Get all of the fields in a single JSON object. Note, if no values were provided for the
     * optional fields via the Builder, the JSON object will include the empty string for those
     * fields.
     *
     * Keys: username, password, first, last, email
     *
     * @return all of the fields in a single JSON object
     */
    public JSONObject asJSONObject() {
        //build the JSONObject
        JSONObject msg = new JSONObject();
        try {
            msg.put("username", getUsername());
            msg.put("password", mPassword);
            msg.put("first", getFirstName());
            msg.put("last", getLastName());
            msg.put("email", getEmail());
        } catch (JSONException e) {
            Log.wtf("CREDENTIALS", "Error creating JSON: " + e.getMessage());
        }
        return msg;
    }

}
