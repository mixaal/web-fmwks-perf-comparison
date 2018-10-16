package net.mixaal.poc.javaulfius;

import org.graalvm.nativeimage.Isolate;
import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.StackValue;
import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.constant.CConstant;
import org.graalvm.nativeimage.c.function.*;
import org.graalvm.nativeimage.c.struct.CField;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.word.Pointer;
import org.graalvm.word.PointerBase;
import org.graalvm.word.WordFactory;

import java.util.Arrays;
import java.util.List;


public class JavaUlfius {

    @CContext(Ulfius.UDirectives.class)
    public static final class Ulfius  {
        @CStruct(value = "_u_instance", addStructKeyword = true)
        interface Instance extends PointerBase {
            @CField
            int port();

            @CField
            int status();

            @CField
            int nb_endpoints();

        }

        @CStruct(value = "_u_response", addStructKeyword = true)
        interface Response extends PointerBase {

        }

        @CStruct(value = "_u_request", addStructKeyword = true)
        interface Request extends PointerBase {

        }

        /**
         * int ulfius_init_instance(
         *      struct _u_instance * u_instance,
         *      unsigned int port,
         *      struct sockaddr_in * bind_address,
         *      const char * default_auth_realm
         *      );
         */
        @CFunction
        static native int ulfius_init_instance(Instance instance, int port, Pointer bind_addr_in, Pointer realm);


        @CFunction
        static native int ulfius_stop_framework(Instance instance);

        @CFunction
        static native int ulfius_start_framework(Instance instance);

        @CFunction
        static native int ulfius_clean_instance(Instance instance);


        @CFunction
        static native int ulfius_set_string_body_response(Response response, int status, CCharPointer body);

        @CFunction
        static native int ulfius_add_endpoint_by_val(
                Instance instance,
                CCharPointer httpMethod,
                CCharPointer prefix,
                CCharPointer format,
                int priority,
                Handler handler,
                PointerBase userData
        );

        interface Handler extends CFunctionPointer {

            @InvokeCFunctionPointer
            int invoke(IsolateThread thread, Request request, Response response, Pointer userData);
        }

        /**
         * int ulfius_add_endpoint_by_val(struct _u_instance * u_instance,
         const char * http_method,
         const char * url_prefix,
         const char * url_format,
         unsigned int priority,
         int (* callback_function)(const struct _u_request * request, // Input parameters (set by the framework)
         struct _u_response * response,     // Output parameters (set by the user)
         void * user_data),
         void * user_data);
         *
         */

        @CConstant
        static final native int U_OK();

        @CConstant
        static final native int U_CALLBACK_CONTINUE();

        public  static class UDirectives implements CContext.Directives {
            @Override
            public List<String> getHeaderFiles() {
                return Arrays.asList("<ulfius.h>");
            }

            @Override
            public List<String> getLibraries() {
                return Arrays.asList("ulfius", "microhttpd");
            }
        }
    }

    @CEntryPoint(builtin = CEntryPoint.Builtin.AttachThread)
    private static int attachThread(Ulfius.Request request, Ulfius.Response response, Isolate userData) {
        return Ulfius.U_CALLBACK_CONTINUE();
    }

    private static final CEntryPointLiteral<Ulfius.Handler> attachInstance =
            CEntryPointLiteral.create(JavaUlfius.class,
                    "attachThread",
                    Ulfius.Request.class,
                    Ulfius.Response.class,
                    Isolate.class
            );

    @CEntryPoint
    private static int callback_hello_world(Isolate isolate, Ulfius.Request request, Ulfius.Response response, Pointer userData) {
        try (CTypeConversion.CCharPointerHolder responseBody= CTypeConversion.toCString("hello")) {
            Ulfius.ulfius_set_string_body_response(response, 200, responseBody.get());
            return Ulfius.U_CALLBACK_CONTINUE();
        }
    }

    private static final CEntryPointLiteral<Ulfius.Handler> handlerInstance =
            CEntryPointLiteral.create(JavaUlfius.class,
                    "callback_hello_world",
                    Isolate.class,
                    Ulfius.Request.class,
                    Ulfius.Response.class,
                    Pointer.class
            );

    public static void main(String []args) throws Exception {
        Ulfius.Instance instance  = StackValue.get(Ulfius.Instance.class);
        int initPhaseResult = Ulfius.ulfius_init_instance(instance, 8080, WordFactory.nullPointer(), WordFactory.nullPointer());
        System.out.printf("init result: %d status=%d port=%d nb_epts=%d\n", initPhaseResult, instance.status(), instance.port(), instance.nb_endpoints());


        Isolate currentInstance = CEntryPointContext.getCurrentIsolate();
        /* Call a C function directly. */
//        callback_hello_world(currentThread, WordFactory.nullPointer(), WordFactory.nullPointer(), WordFactory.nullPointer());
        /* Call a C function indirectly via function pointer. */
//        handlerInstance.getFunctionPointer().invoke(currentThread,WordFactory.nullPointer(), WordFactory.nullPointer(), WordFactory.nullPointer() );


        try (
                CTypeConversion.CCharPointerHolder method=CTypeConversion.toCString("GET");
                CTypeConversion.CCharPointerHolder path=CTypeConversion.toCString("/helloworld")){

            int attachResult = Ulfius.ulfius_add_endpoint_by_val(
                    instance,
                    method.get(),
                    path.get(),
                    WordFactory.nullPointer(),
                    0,
                    attachInstance.getFunctionPointer(),
                    currentInstance
            );
            System.out.printf("attach result: %d\n", attachResult);
            int registrationResult = Ulfius.ulfius_add_endpoint_by_val(
                    instance,
                    method.get(),
                    path.get(),
                    WordFactory.nullPointer(),
                    0,
                    handlerInstance.getFunctionPointer(),
                    WordFactory.nullPointer()
            );
            System.out.printf("registration result: %d status=%d port=%d nb_epts=%d\n", registrationResult, instance.status(), instance.port(), instance.nb_endpoints());
            int result = Ulfius.ulfius_start_framework(instance);
            System.out.printf("U_OK()=%d result=%d\n", Ulfius.U_OK(), result);
            System.in.read();
            Ulfius.ulfius_stop_framework(instance);
            Ulfius.ulfius_clean_instance(instance);
        }
    }


    /**
     * /**
     *  * Callback function for the web application on /helloworld url call
     *
    int callback_hello_world (const struct _u_request * request, struct _u_response * response, void * user_data) {
        ulfius_set_string_body_response(response, 200, "Hello World!");
        return U_CALLBACK_CONTINUE;
    }

    /**
     *   main function
     *
    int main(void) {
        struct _u_instance instance;

        // Initialize instance with the port number
        if (ulfius_init_instance(&instance, PORT, NULL, NULL) != U_OK) {
            fprintf(stderr, "Error ulfius_init_instance, abort\n");
            return(1);
        }

        // Endpoint list declaration
        ulfius_add_endpoint_by_val(&instance, "GET", "/helloworld", NULL, 0, &callback_hello_world, NULL);

        // Start the framework
        if (ulfius_start_framework(&instance) == U_OK) {
            printf("Start framework on port %d\n", instance.port);

            // Wait for the user to press <enter> on the console to quit the application
            getchar();
        } else {
            fprintf(stderr, "Error starting framework\n");
        }
        printf("End framework\n");

        ulfius_stop_framework(&instance);
        ulfius_clean_instance(&instance);

        return 0;
    }

     */
}
