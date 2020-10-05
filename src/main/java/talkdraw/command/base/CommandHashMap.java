package talkdraw.command.base;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.yaml.snakeyaml.Yaml;

import talkdraw.App;
import talkdraw.misc.ConsoleColors;

public class CommandHashMap extends HashMap<String, Command>{
    public CommandHashMap(App APP){
        System.out.println( ConsoleColors.SEPERA_LINE );
        System.out.println( ConsoleColors.INFO + "Start Initial Command Set" );
        //創建 yaml Class
        Yaml yaml = new Yaml();

        //將 yaml 檔案讀入成 InputString
        InputStream is = getClass().getResourceAsStream("/command/command.yml");
        
        //迭代取得 yaml 的所有指令資料
        for(Object obj : yaml.loadAll( is )){

            //強制轉型，將 obj 轉成 LinkedHashMap
            LinkedHashMap<String, Object> objMap = (LinkedHashMap<String, Object>)obj;
            
            //取得指令的名稱
            String className = objMap.get("name").toString();

            //開始創建類別
            try{
                //取得指令的屬性
                CommandAttribute attribute = new CommandAttribute( ((LinkedHashMap<String, Object>)objMap.get("attribute")) );

                //將讀入的檔案創建成 Class (其實就是動態鏈結 Class)
                Class<?> cls = Class.forName( String.join(".", "talkdraw.command",className ) );
                Object commandObj = cls.getConstructor( App.class, CommandAttribute.class ).newInstance(  
                    APP, 
                    attribute 
                );    

                //將指令放入至 Map 裡
                put( className , (Command)commandObj );
            }
            catch( Exception e ){
                System.out.println( ConsoleColors.ERROR + className + " Initialize Error！\n\n" );
                e.printStackTrace();
            }
        }
    }

    /** <p>重寫了原本的 {@link HushMap} 的 {@link #HushMap.getKey()}</p>
     *  <p>會多判斷此指令的 {@code 英文名稱} 與 {@code 中文名稱}</p>
     *  <p>================================================</p>
     *  {@inheritDoc} */
    @Override public Command get(Object key){
        //判斷有沒有包含此 key
        if( containsKey( key ) )return super.get( key );

        //判斷這個 key 是否是其他的中文名稱 or 英文名稱 
        String keyS = ( String )key;
        for(String k : keySet() ){
            Command cmd = super.get( k );
            if( keyS.equalsIgnoreCase( cmd.getEnglishName() ) || keyS.equals( cmd.getChineseName() ) ){
                return super.get( k );
            }
        }
        return null;
    }
}