package com.datio.pythonwrapper;

import org.python.core.Py;
import org.python.core.PyFile;
import org.python.core.PySystemState;
import org.python.core.imp;
import org.python.util.InteractiveConsole;

import java.util.Properties;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) {

        System.out.println( "Start Jython program" );


        PySystemState.initialize(PySystemState.getBaseProperties(),
                new Properties(), args);

        InteractiveConsole c = createInterpreter(checkIsInteractive());
        c.execfile("src/main/resources/hola.py");
    }

    protected static boolean checkIsInteractive() {
        PySystemState systemState = Py.getSystemState();
        boolean interactive = ((PyFile) Py.defaultSystemState.stdin).isatty();
        if (!interactive) {
            systemState.ps1 = systemState.ps2 = Py.EmptyString;
        }
        return interactive;
    }

    protected static InteractiveConsole createInterpreter(boolean interactive) {
        InteractiveConsole c = newInterpreter(interactive);
        Py.getSystemState().__setattr__("_jy_interpreter", Py.java2py(c));

        imp.load("site");
        return c;
    }

    private static InteractiveConsole newInterpreter(boolean interactiveStdin) {
        if (!interactiveStdin) {
            return new InteractiveConsole();
        }

        String interpClass = PySystemState.registry.getProperty(
                "python.console", "");
        if (interpClass.length() > 0) {
            try {
                return (InteractiveConsole) Class.forName(interpClass)
                        .newInstance();
            } catch (Throwable t) {
                // fall through
            }
        }
        //return new JLineConsole("UTF-8"); //maw: change return
        return new InteractiveConsole();
    }
}
