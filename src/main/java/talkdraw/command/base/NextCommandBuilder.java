package talkdraw.command.base;

import talkdraw.command.base.Command.Phase;

/** <p>用來 建構 {@link NextCommand} 存放下一個指令的 Class</p> 
 *  <p>注意：他不能直接轉換成 {@link Command}</p>
 *  <p>請確定：</p>
 *  <pre>public Command toCommand( HashMap )</pre>
 *  <p>來取得下一個指令</p>*/
public class NextCommandBuilder {
    /** 下一個指令的 Class 型別  */
    private Class<?> cmdclass;
    /** 下一個指令的階段 */
    private Phase phase;

    /** 用來設定是否要將 {@code 上} 個指令 {@link Command} 推入指令堆疊裡*/
    private boolean isStack;

    /** 是否要清空使用者當前的所有輸入 */
    private boolean isClearArgs;

    /** {@link NextCommandBuilder} 建構子 
     *  <p>預設 {@code 上} 個指令{@link Command}不進入堆疊</p>
     *  <p>預設 不清空使用者目前的所有輸入</p>
     *  @param nextCmdClass 下一個要執行的 {@link Command} Class
     *  @param phase 下一個指令執行時的階段*/
    public NextCommandBuilder( Class<?> nextCmdClass, Phase phase ){
        this.cmdclass = nextCmdClass;
        this.phase = phase;
        this.isStack = false;
        this.isClearArgs = false;
    }

    /** 設定下個要執行的指令{@link Command}
     *  @param nextCmdClass 下個要執行的指令 {@link Class}*/
    public NextCommandBuilder setNextCommand( Class<?> nextCmdClass ){
        this.cmdclass = nextCmdClass;
        return this;
    }
    /** 設定下個要執行的指令階段{@link Phase}
     *  @param nextCmdClass 下個要執行的指令 {@link Class}*/
    public NextCommandBuilder setNextCommandPhase( Phase nextPhase ){
        this.phase = nextPhase;
        return this;
    }
    /** 設定是否要將 {@code 上} 個指令 {@link Command} 推入指令堆疊裡
     *  @param isStack 下個要執行的指令 {@link Class}*/
    public NextCommandBuilder setIsStackPreCommand( boolean isStack ){
        this.isStack = isStack;
        return this;
    }
    /** 設定是否要先清空使用者輸入
     *  @param isClearArgs 下個要執行的指令 {@link Class}*/
    public NextCommandBuilder setIsClearArgs( boolean isClearArgs ){
        this.isClearArgs = isClearArgs;
        return this;
    }

    /** 建構 {@link NextCommand} */
    public NextCommand build(){
        return new NextCommand(cmdclass, phase, isStack, isClearArgs);
    }
}