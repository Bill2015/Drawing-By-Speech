package talkdraw.command.base;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/** <p>使用於 {@code Resources} 的 Command.yml</p> 
 *  <p>將裡面的資訊給 Load 包裝成這個 Class</p>*/
public class CommandAttribute {

    /** Yaml 的屬性集(Attribute) */
    private final String description, englishName, chineseName, usage;

    private final boolean canTemp;

    private final ArrayList<String> args;
    /**建構子 
     *  @param map 指令的屬性集 (Attribute Map)*/
    public CommandAttribute( LinkedHashMap<String, Object> map ){
        this.description = map.get( "description" ).toString();
        this.englishName = map.get( "englishName" ).toString();
        this.chineseName = map.get( "chineseName" ).toString();
        this.canTemp     = (boolean)map.get( "useTemporary" );
        this.args        = (ArrayList<String>)map.get( "args" );
        this.usage       = map.get( "usage" ).toString();
    }

    /** 取得指令描述 @return 描述 {@code [String]} */
    public String getDescription(){ return description; }
    /** 取得指令英文名稱 @return 英文 {@code [String]} */
    public String getEnglishName(){ return englishName; }
    /** 取得指令中文名稱 @return 中文 {@code [String]} */
    public String getChineseName(){ return chineseName; }
    /** 取得指令操作用法 @return 操作 {@code [String]} */
    public String getUsage(){ return usage; }
    /** 取得指令的其他參數 @return 參數陣列 */
    public ArrayList<String> getArgsList(){ return args; }
    /** 取得此指令是否可以暫存 @return [Boolean] */
    public boolean getCanTemporary(){ return canTemp; }
}