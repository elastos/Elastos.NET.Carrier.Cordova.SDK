/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.essentials.plugins.carrier;

import android.content.res.AssetManager;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.io.InputStream;
import java.io.IOException;

class BootstrapsGetter {
    @SerializedName("bootstraps")
    ArrayList<BootstrapNode> bootstrapNodes;

    static class BootstrapNode {
        @SerializedName("ipv4")
        public String ipv4;

        @SerializedName("port")
        public int port;

        @SerializedName("publicKey")
        public String publicKey;
    }

    @SerializedName("expressNodes")
    ArrayList<ExpressNode0> expressNode0s;

    static class ExpressNode0 {
        @SerializedName("ipv4")
        public String ipv4;

        @SerializedName("port")
        public int port;

        @SerializedName("publicKey")
        public String publicKey;
    }

    static public BootstrapsGetter getGetter(CarrierPlugin plugin) {
        AssetManager am = plugin.cordova.getActivity().getAssets();

        String jsonResName;
        try {
            InputStream inputStream = am.open("bootstraps.json");
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            jsonResName = new String(bytes);
        } catch (IOException e) {
            return null;
        }

        return new Gson().fromJson(jsonResName, BootstrapsGetter.class);
    }
}
