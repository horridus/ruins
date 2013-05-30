package cek.ruins;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class ScriptExecutor {
	private ScriptableObject sharedScope;
	private Map<String, String> libraries = new HashMap<String, String>();
	
	/**
	* SingletonHolder is loaded on the first execution of Singleton.getInstance() 
	* or the first access to SingletonHolder.INSTANCE, not before.
	*/
	private static class SingletonHolder { 
	        public static final ScriptExecutor INSTANCE = new ScriptExecutor();
	}
	
	public static ScriptExecutor executor() {
		return SingletonHolder.INSTANCE;
	}
	
	public Script compileScript(String source, String sourceName) {
		Context ctx = Context.enter();
		Script script = ctx.compileString(source, sourceName, 1, null);
		Context.exit();
		
		return script;
	}
	
	public void loadLibraries(File directory) throws IOException {
		File[] librariesFiles = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".js");
			}
		});
		
		for (File library : librariesFiles) {
			String libraryName = library.getName().substring(0, library.getName().lastIndexOf(".js"));
			loadLibrary(library, libraryName, "UTF-8");
		}
	}
	
	public void loadLibrary(File library, String sourceName, String encoding) throws IOException {
		String source = FileUtils.readFileToString(library, encoding);
		
		this.libraries.put(sourceName, source);
		
//		Context ctx = Context.enter();
//		Object libraryObject = ctx.evaluateString(sharedScope, source, sourceName, 0, null);
//		this.libraries.put(sourceName, libraryObject);
//		Context.exit();
	}
	
	public Object executeScript(Script script, Map<String, Object> objs) {
		Context ctx = Context.enter();
		ScriptableObject instanceScope = this.prepareInstanceScope(ctx);
		
		for (Map.Entry<String, Object> entry : objs.entrySet()) {
			addObj(instanceScope, entry.getValue(), entry.getKey());
		}
		
		//inject libraries objects into instance scope
		for (Map.Entry<String, String> entry : this.libraries.entrySet()) {
			Object libraryObject = ctx.evaluateString(instanceScope, entry.getValue(), entry.getKey(), 0, null);
			addObj(instanceScope, libraryObject, entry.getKey());
		}
		
		//TODO capire come e quando fare seal
		//instanceScope.sealObject();
		
		//TODO gestire eventuale valore di ritorno
		Object result = script.exec(ctx, instanceScope);
		
		Context.exit();
		
		return result;
	}
	
	public NativeArray convertToJSArray(Object[] array) {
		Context ctx = Context.getCurrentContext();
		return (NativeArray) ctx.newArray(this.sharedScope, array);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Scriptable registerAndCreateObject(Class clazz, Object[] constructorArgs, boolean sealed) throws Exception {
		ScriptableObject.defineClass(this.sharedScope, clazz, sealed);
		
		Context ctx = Context.enter();
		Scriptable newObj = ctx.newObject(this.sharedScope, clazz.getSimpleName(), constructorArgs);
		Context.exit();
		
		return newObj;
	}
	
	private ScriptExecutor() {
		Context ctx = Context.enter();
		this.sharedScope = ctx.initStandardObjects(null, true);

		//TODO add common objects
		//this.sharedScope.sealObject();
		Context.exit();
	}
	
	private ScriptableObject prepareInstanceScope(Context ctx) {
		ScriptableObject instanceScope = (ScriptableObject) ctx.newObject(this.sharedScope);
	    instanceScope.setPrototype(this.sharedScope);
	    instanceScope.setParentScope(null);
	    
	    return instanceScope;
	}
	
	private void addObj(ScriptableObject scope, Object object, String jsObjectName) {
		Object value = Context.javaToJS(object, scope);
		ScriptableObject.putProperty(scope, jsObjectName, value);
	}
}
