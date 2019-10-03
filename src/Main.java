import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EmptyStackException;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws Exception {

        Class<?> cl = null;
        Method firstMethod = null;
        try {

            if (args.length == 0) {
                throw new Exception("You should enter at least one argument!!");
            } else {
                String className = "models." + args[0];
                cl = Class.forName(className);
            }

        } catch (ClassNotFoundException e) {

            System.out.printf("The class %s is not accessible %n", args[0]);
            e.printStackTrace();
        }
        assert cl != null;
        Method[] methods = cl.getMethods();
        try {

            firstMethod = methods[0];
        } catch (EmptyStackException e) {

            System.out.printf("\nThe class %s does not have any method!%n", cl.getName());
            e.printStackTrace();
        }

        int numberOfArguments = firstMethod.getParameterCount();
        Class[] typeOfParameters = firstMethod.getParameterTypes();

        /*
          Do whatever should be done here! < === from here
         */
        if (args.length == (numberOfArguments + 1)) {
            var argumentsAsObjectArray = getArguments(Stream.of(args).skip(1).toArray(String[]::new), typeOfParameters);

            examiner(typeOfParameters, argumentsAsObjectArray);

            for (int index = 0; index < numberOfArguments; index++) {
                System.out.println(parser(typeOfParameters[index].getName()));
            }
            /*
            If everything work flawlessly then, here should make instance of the class and
            receives the arguments through terminal and executes the first method of the given class
             */

            Object obj = cl.getConstructor().newInstance(); // Subject to change
            System.out.println(firstMethod.invoke(obj, argumentsAsObjectArray));// Subject to change
            /*
           Till here ==== >
            */
        } else if (args.length == 1) {

            System.out.println(parser(firstMethod.toGenericString()));
        } else {
            throw new Exception("The input arguments and the need of the class's method are mismatched.");
        }

    }

    /**
     * To eliminate some unnecessary details in the given strings.
     *
     * @param input
     * @return
     */
    static private String parser(String input) {
        return input
                .replace("java.", "")
                .replace("lang.", "")
                .replace("util.", "")
                .replace("com.webService.", "");
    }

    /**
     * To check whether the input list of the arguments' type are given correctly or not.
     *
     * @param typeOfParameters
     * @param args
     * @return
     */
    static private void examiner(Class[] typeOfParameters, Object[] args) throws Exception {
        if ( typeOfParameters.length != args.length) {
            throw new Exception("Argument count does not match target method signature");
        }

        var typeMismatch = IntStream.range(0, typeOfParameters.length)
                .mapToObj(x -> {
                    // Fails to understand what int and Integer are the same
                    // typeOfParameters[x].isInstance(args[x]);
                    return typeOfParameters[x].isInstance(args[x]);
                })
                .reduce(true, (x, y) -> x && y);

        //if (!typeMismatch) {
        //    throw new Exception("Argument types does not match method signature");
        //}
    }

    /**
     * To make an array of objects for feeding the invoking method of the class's first method.
     * @param args
     * @param classArray
     * @return array of objects that should be comprised of something like this, e.g. {new String("Hello Amir"), 25}
     */
    static private Object[] getArguments(String[] args, Class[] classArray) {
        var count = args.length;

        return IntStream.range(0, count)
                .mapToObj(x -> toObject(classArray[x], args[x]))
                .toArray();
    }

    private static Object toObject(Class clazz, String value) {
        try {
            if (String.class == clazz) return value;
            if (Boolean.class == clazz || boolean.class == clazz) return Boolean.parseBoolean(value);
            if (Byte.class == clazz || byte.class == clazz) return Byte.parseByte(value);
            if (Short.class == clazz || short.class == clazz) return Short.parseShort(value);
            if (Integer.class == clazz || int.class == clazz) return Integer.parseInt(value);
            if (Long.class == clazz || long.class == clazz) return Long.parseLong(value);
            if (Float.class == clazz || float.class == clazz) return Float.parseFloat(value);
            if (Double.class == clazz || double.class == clazz) return Double.parseDouble(value);
        } catch (Exception e) {
            // e.printStackTrace();
            // throw new Exception(String.format("Failed to convert %s to type %s", value, clazz.getName()));
            System.out.println(String.format("Failed to convert %s to type %s", value, clazz.getName()));
            return null;
        }

        return null;
    }
}