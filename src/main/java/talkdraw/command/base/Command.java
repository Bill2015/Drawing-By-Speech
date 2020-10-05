package talkdraw.command.base;

import talkdraw.App;


/** <p>所有指令之母，要新增指令都需要繼承這個 Class</p> 
 *  <p>提供：</p>
 * <blockquote><pre>
 *public NextCommandPack execute()      //執行用函式
 *protected NextCommandPack success()   //成功函式
 *protected NextCommandPack failure()   //失敗函式
 *
 * //指令漸進列舉(不一定每個都會使用到，根據 "需求" 而定)
 * public enum Phase{
 *       None,          //最初始的步驟
 *       Get_ID,        //當指令需要取得 "ID" 
 *       Get_Name,      //當指令需要取得 "Name" 
 *       Get_MOVED,     //當指令需到 "做動作" 
 *       Get_Keyword,   //當指令需要取得 "關鍵字"
 *       Get_Value      //當指令需要取得 "數值"
 *   }
 * </pre></blockquote>
*/
public abstract class Command{
    protected App APP;
    /** 關於指令的描述 {@code [String]}*/
    protected final String description;
    /** 此指令的英文名稱 {@code [String]} */
    protected final String englishName;
    /** 此指令的中文名稱 {@code [String]} */ 
    protected final String chineseName;
    /** 此指令的使用方法 {@code [String]} */ 
    protected final String usage;
    /** 是否可以將此指令暫存 */
    protected final boolean canSaveTemp;
    /** 目前指令執行的階段 {@link Phase} */
    protected Phase phase;
    public Command(App APP, CommandAttribute yamlLoader){
        this.APP = APP;
        this.phase = Phase.None;

        this.usage = yamlLoader.getUsage();
        this.description = yamlLoader.getDescription();
        this.englishName = yamlLoader.getEnglishName();
        this.chineseName = yamlLoader.getChineseName();
        this.canSaveTemp = yamlLoader.getCanTemporary();
    }
    /** 執行此指令 
     *  @param args 參數值({@code 根據指令而有不同的參數值})
     *  @return 回傳下一個指令 {@link NextCommand}*/
    public abstract NextCommand execute( String ... args );
    /** 成功執行指令
     *  @param args 參數值({@code 根據指令而有不同的參數值})
     *  @return 回傳下一個指令 {@link NextCommand}*/
    protected abstract NextCommand success( String ... args );
    /** 執行指令失敗
     *  @param args 參數值({@code 根據指令而有不同的參數值})
     *  @return 回傳下一個指令 {@link NextCommand}*/
    protected abstract NextCommand failure( String ... args );
    /** 執行被暫存時的動作
     *  @param args 參數值({@code 根據指令而有不同的參數值})
     *  @return 回傳下一個指令 {@link NextCommand}*/
    public abstract NextCommand doTemporaryAction( String ... args );

    /** <p>初始化此指令</p> 
     *  <p>這個主要的用途只有當使用者講出一個"新"的指令時才需要初始化</p>*/
    public void initial(){
        phase = Phase.None;
    }

    /** 設定目前指令的階段 {@link Phase} 
     *  @param inPhase 欲設定的階段*/
    protected void setPhase(Phase inPhase){
        phase = inPhase;
    }

    /** 取得英文名稱  
     *  @return 英文名稱 {@code [String]}*/
    public String getEnglishName(){ return englishName; }

    /** 取得中文名稱  
     *  @return 中文名稱 {@code [String]}*/
    public String getChineseName(){ return chineseName; }

    /** 取得目前指令的階段 
     *  @return 階段 {@code [Phase]}*/
    public Phase getPhase(){ return phase; }

    /** <p>漸進式的 指令({@link Command}) 階段</p> 
     *  <p>基本上只是拿來做每個指令的階段定義</p>
     *  <p>沒有真實的意思，只要你想，可以把 {@code Get_ID} 當作 {@code Get_Value}</p>
     *  <p>當然不推薦這麼做就是了</p>*/
    public enum Phase{
        /** 最初始的步驟 */
        None("無"),          
        /** 當指令需要取得 "ID"  */
        Get_ID("ID"),        
        /** 當指令需要取得 "Name"  */
        Get_Name("名稱"),      
        /** 當指令需到 "做動作"  */
        Get_MOVED("動作"),     
        /** 當指令需要取得 "關鍵字" */
        Get_Web_Keyword("關鍵字"),
        /** 當指令需要取得 "自然語言" */   
        Get_Natural_String("自然語言"),
        /** 當指令需要取得 "數值" */
        Get_Value("數值"),     
        /** 當指令需要取得 "名稱" or "標籤" */
        Get_Name_Or_Tag("名稱"),
        /** 當指令需要取得 "動作" */
        Get_Action("動作"),
        /** 當指令需要取得 "方向" */
        Get_DIRECT("方向"),
        /** 當指令需要取得 "工具屬性" */
        Get_Ditail_Name("屬性名稱"),
        /** 當指令需要取得 "顏色" */
        Get_Color("顏色"),
        /** 當指令需要取得 "是否確認" */
        Get_Comfirm("是否確定");
        private String name;
        Phase( String name ){
            this.name = name;
        }
        /** 取得指令動作名稱 
         *  @return 名稱 {@code [String]}*/
        public String getName(){
            return name;
        }
    }
}

