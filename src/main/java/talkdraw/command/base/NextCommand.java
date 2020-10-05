package talkdraw.command.base;

import java.util.HashMap;

import talkdraw.command.DoNothingCommand;
import talkdraw.command.base.Command.Phase;

/** <p>用來存放下一個指令的 Class</p> 
 *  <p>注意：他不能直接轉換成 {@link Command}</p>
 *  <p>請使用：</p>
 *  <pre>public Command toCommand( HashMap )</pre>
 *  <p>來取得下一個指令</p>*/
public class NextCommand{
    /** 靜態的 DO_NOTHING 指令 */
    public static NextCommand DO_NOTHING = new NextCommand(DoNothingCommand.class, Phase.None);

    /** 下一個指令的 Class 型別  */
    private Class<?> cmdclass;
    /** 下一個指令的階段 */
    private Phase phase;

    /** 用來設定是否要將 {@code 上} 個指令 {@link Command} 推入指令堆疊裡*/
    private final boolean isStack;

    /** 是否要清空使用者當前的所有輸入 */
    private final boolean isClearArgs;
    /** {@link NextCommand} 建構子 
     *  <p>預設 {@code 上} 個指令{@link Command}不進入堆疊</p>
     *  <p>預設 不清空使用者目前的所有輸入</p>
     *  @param nextCmdClass 下一個要執行的 {@link Command} Class
     *  @param phase 下一個指令執行時的階段*/
    public NextCommand( Class<?> nextCmdClass, Phase phase ){
        this.cmdclass = nextCmdClass;
        this.phase = phase;
        this.isStack = false;
        this.isClearArgs = false;
    }
    /** {@link NextCommand} 建構子
     *  @param nextCmdClass 下一個要執行的 {@link Command} Class
     *  @param phase 下一個指令執行時的階段
     *  @param isStack 設定 {@code 上} 個指令是否要進入堆疊 {@code Stack}
     *  @param refreshArgs 設定是否要清空使用者目前輸入的所有 Args */
    public NextCommand( Class<?> nextCmdClass, Phase phase, boolean isStack, boolean refreshArgs ){
        this.cmdclass = nextCmdClass;
        this.phase = phase;
        this.isStack = isStack;
        this.isClearArgs = refreshArgs;
    }
    /** 取得下一個指令 
     *  @param map 指令集 HashMap
     *  @return 回傳下一個指令 {@code Command}*/
    public Command toCommand( HashMap<String, Command> map ){ 
        Command command = map.get( cmdclass.getSimpleName() );
        command.setPhase( phase );
        return command; 
    }
    /** 取得下個指令的階段 */
    public Phase getPhase(){ return phase; }

    /** 取得 {@code 上} 個指令 {@link Command}   是否要進入堆疊
     *  @return {@code true = 要} | {@code false = 否} {@code [Boolean]}*/
    public boolean isStack(){ return isStack; }

    public Class<?> getNowCmdClass(){ return cmdclass; }
    /** 取得是否要先清空使用者輸入
     *  @return {@code true = 要} | {@code false = 否} {@code [Boolean]}*/
    public boolean isClearArgs(){ return isClearArgs; }
}