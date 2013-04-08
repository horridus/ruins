package cek.ruins;

import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import cek.ruins.world.locations.Settlement;

public class ScriptExecutor {
	private ScriptableObject sharedScope;
	
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
	
	private ScriptExecutor() {
		Context ctx = Context.enter();
		this.sharedScope = ctx.initStandardObjects(null, true);
		
		//TODO add common objects
		
		
		this.sharedScope.sealObject();
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
	
	public Script compileScript(String source, String sourceName) {
		Context ctx = Context.enter();
		Script script = ctx.compileString(source, sourceName, 1, null);
		Context.exit();
		
		return script;
	}
	
	public Object executeScript(Script script, Map<String, Object> objs) {
		Context ctx = Context.enter();
		ScriptableObject instanceScope = this.prepareInstanceScope(ctx);
		
		for (Map.Entry<String, Object> entry : objs.entrySet()) {
			addObj(instanceScope, entry.getValue(), entry.getKey());
		}
		
		//TODO capire come e qunado fare seal
		//instanceScope.sealObject();
		
		//TODO gestire eventuale valore di ritorno
		Object result = script.exec(ctx, instanceScope);
		
		Context.exit();
		
		return result;
	}
	
	public Scriptable convertToJSArray(Object[] array) {
		Context ctx = Context.getCurrentContext();
		return ctx.newArray(ctx.initStandardObjects(), array);
	}
	
	//TODO remove??
	public void executeSettlementsArchitectScripts(String script, Settlement settlement) {
		Context ctx = Context.enter();
		
		ScriptableObject instanceScope = this.prepareInstanceScope(ctx);
		
		this.addObj(instanceScope, settlement, "settlement");
	    
		instanceScope.sealObject();
		
	    ctx.evaluateString(instanceScope, script, "settlementsArchitect", 1, null);
		
		Context.exit();
	}
}
