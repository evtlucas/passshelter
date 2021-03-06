/*
Copyright 2015 Everton Luiz de Resende Lucas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package br.unisinos.evertonlucas.passshelter.util;

import android.content.Context;
import android.util.Log;

import br.unisinos.evertonlucas.passshelter.R;

/**
 * Class responsible for show and log exceptions
 * Created by everton on 07/10/15.
 */
public class ShowLogExceptionUtil {

    public static void showAndLogException(Context context, String message, Exception e) {
        ToastUtil.showToastMessage(context, message);
        Log.e(context.getResources().getString(R.string.app_name), message, e);
    }

    public static void logException(Context context, String message, Exception e) {
        Log.e(context.getResources().getString(R.string.app_name), message, e);
    }
}
