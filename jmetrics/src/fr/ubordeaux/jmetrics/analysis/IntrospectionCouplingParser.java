package fr.ubordeaux.jmetrics.analysis;

import fr.ubordeaux.jmetrics.metrics.GranularityScale;
import fr.ubordeaux.jmetrics.metrics.ClassGranularity;
import fr.ubordeaux.jmetrics.project.ClassFile;
import fr.ubordeaux.jmetrics.project.ProjectStructure;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An implementation of the {@link CouplingParser} interface that uses introspection.
 */
public class IntrospectionCouplingParser extends IntrospectionParser implements CouplingParser {

    @Override
    public List<Dependency> getInheritanceDependencies(ClassFile srcFile) {
        Class<?> srcClass = getClassFromFile(srcFile);
        Class<?> superClass = srcClass.getSuperclass();
        Class<?>[] interfaces = srcClass.getInterfaces();

        ArrayList<Class<?>> efferentDependencies = new ArrayList<>(Arrays.asList(interfaces));
        if(superClass != null) {
            efferentDependencies.add(superClass);
        }

        List<ClassFile> projectClasses = ProjectStructure.getInstance().getClasses();
        List<GranularityScale> dstClasses = findEfferentDependenciesInProject(efferentDependencies, projectClasses);
        return generateDependenciesList(new ClassGranularity(srcFile), DependencyType.Inheritance, dstClasses);
    }

    @Override
    public List<Dependency> getAggregationDependencies(ClassFile srcFile) {
        Class<?> srcClass = getClassFromFile(srcFile);

        ArrayList<Class<?>> efferentDependencies = new ArrayList<>();
        for (Field field: srcClass.getDeclaredFields()) {
            Class<?> type = field.getType();
            if (!type.isPrimitive() && !efferentDependencies.contains(type) && srcClass != type) {
                efferentDependencies.add(type);
            }
        }

        List<ClassFile> projectClasses = ProjectStructure.getInstance().getClasses();
        List<GranularityScale> dstClasses = findEfferentDependenciesInProject(efferentDependencies, projectClasses);
        return generateDependenciesList(new ClassGranularity(srcFile), DependencyType.Aggregation, dstClasses);
    }

    @Override
    public List<Dependency> getSignatureDependencies(ClassFile srcFile) {
        Class<?> srcClass = getClassFromFile(srcFile);

        ArrayList<Class<?>> efferentDependencies = new ArrayList<>();
        ArrayList<Class<?>> methodDependencies = new ArrayList<>();
        for (Method method: srcClass.getDeclaredMethods()) {
            Class<?>[] exceptions = method.getExceptionTypes();
            Class<?>[] parameters = method.getParameterTypes();
            Class<?> returnType = method.getReturnType();

            methodDependencies.addAll(Arrays.asList(exceptions));
            methodDependencies.addAll(Arrays.asList(parameters));
            methodDependencies.add(returnType);
        }

        for (Class<?> dep: methodDependencies) {
            if (!dep.isPrimitive() && !efferentDependencies.contains(dep) && srcClass != dep) {
                efferentDependencies.add(dep);
            }
        }

        List<ClassFile> projectClasses = ProjectStructure.getInstance().getClasses();
        List<GranularityScale> dstClasses = findEfferentDependenciesInProject(efferentDependencies, projectClasses);
        return generateDependenciesList(new ClassGranularity(srcFile), DependencyType.Signature, dstClasses);
    }

    @Override
    public List<Dependency> getInternalDependencies(ClassFile srcFile) {
        // Internal dependencies is not accessible through the reflect standard library.
        return new ArrayList<>();
    }



    /**
     * Determine the list of efferent dependencies class that is present in project classes.
     * @param efferentDependencies List of class where the analyzed class depend upon.
     * @param projectClasses List of all class files in the project.
     * @return List of GranularityScale (Elementary) such as efferent dependencies is in the project.
     */
    private List<GranularityScale> findEfferentDependenciesInProject(List<Class<?>> efferentDependencies,
                                                                     List<ClassFile> projectClasses) {
        List<GranularityScale> matchDependencies = new ArrayList<>();
        for (Class<?> classEff: efferentDependencies) {
            for (ClassFile dstFile: projectClasses) {
                Class<?> dstClass = getClassFromFile(dstFile);
                if (dstClass.getName().equals(classEff.getName())) {
                    GranularityScale dst = new ClassGranularity(dstFile);
                    matchDependencies.add(dst);
                }
            }
        }
        return matchDependencies;
    }

    /**
     * Generate a list of dependencies from a source category, a dependency type and a list of destination categories.
     * @param src The source category of the dependencies.
     * @param type The type of the dependencies.
     * @param dstList The list of destination class categories.
     * @return The list of generated dependencies.
     */
    private List<Dependency> generateDependenciesList(GranularityScale src, DependencyType type,
                                                      List<GranularityScale> dstList) {
        List<Dependency> dependencies = new ArrayList<>();
        for (GranularityScale dst: dstList) {
            dependencies.add(new Dependency(src, dst, type));
        }
        return dependencies;
    }

}
