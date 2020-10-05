package talkdraw;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

import javafx.application.Platform;
import javafx.concurrent.Task;
import talkdraw.command.DoNothingCommand;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandHashMap;
import talkdraw.command.base.NextCommand;
import talkdraw.command.base.Command.Phase;
import talkdraw.componet.PagePane;
import talkdraw.misc.ConsoleColors;
import talkdraw.tools.EraserTool;
import talkdraw.tools.PencilTool;
import talkdraw.tools.Tool;


public class Progression extends Task<Void>{
    private App APP;
    /** 現在的指令 */
    private Command nowCommand;
    /** 輸入的數值串列 */
    private LinkedList<String> argList = new LinkedList<>();

    /** <p>指令集</p> 
     *  <p>繼承了 {@link HushMap}</p>
     *  <p>由 Resourse 裡的 Command.yml 抓取指令集</p>*/
    private final CommandHashMap commandMap;

    /** <p>有些指令需要與其他指令做搭配</p> 
     *  <p>像是圖片的操作</p>
     *  <p>假如這時使用者未選取出目前操作的圖片</p>
     *  <p>此時指令就必須這樣跑 {@code SetOpacity -> SelectImage -> SetOpactiy}</p>
     *  <p>也就是為什麼需要 {@link Stack} 的原因</p>*/
    private Stack<Command> commandStack = new Stack<>();

    private ArrayList<Command> tempCommandList = new ArrayList<>();

    public Progression(App APP){
        this.APP = APP;
        this.commandMap = APP.getCommandSet();

        nowCommand = commandMap.get("DoNothingCommand");

        tempCommandList.add( commandMap.get("SetDetailCommand") );
        tempCommandList.add( commandMap.get("ChangeToolCommand") );
    }

    @Override
    public Void call() throws Exception{

        System.out.println( ConsoleColors.SEPERA_LINE );
        System.out.println( ConsoleColors.SUCCESS + "開始執行 Main Progression Thread");

        try{

    
            while(true){

            
                //Background work          
                //final CountDownLatch latch = new CountDownLatch(1);    
                
                System.out.println( ConsoleColors.INFO + "Waiting Command Voice Singal " + System.currentTimeMillis() );
                
                //判斷語音輸入 or 文字輸入 是否為空
                if( !APP.SOCKET_SEVER.isReceived() && !APP.TEXT_PANE.hasInput() && commandStack.size() <= 0 ){
                    Thread.sleep( 500 );
                    APP.STATUS_PANE.animation();
                    APP.SOCKET_SEVER.getFloatingPane().animation();
                    continue;
                }
            
                //假如指令 Stack 有指令
                if( commandStack.size() > 0 ){
                    nowCommand = commandStack.pop().execute( "" ).toCommand( commandMap );
                    APP.STATUS_PANE.changeBothText( nowCommand.getChineseName(), nowCommand.getPhase().getName() );
                }
                //-----------------------------------------------------------------
                //假如有接收到 語音資訊 or 文字輸入
                else if( APP.SOCKET_SEVER.isReceived() || APP.TEXT_PANE.hasInput() ){

                    if( APP.SOCKET_SEVER.isReceived() ){

                        //假如他是垃圾訊息就印出所有字串 (不執行)
                        if( APP.SOCKET_SEVER.isSpamMsg() ){
                            String temp = APP.SOCKET_SEVER.getFormatWholeMsg();
                            APP.INFO_PANE.println( temp );
                            continue;
                        }

                        //判斷 Python 讀入的字串是否有多個，有多個就直接存到 List 裡
                        argList.addAll(  APP.SOCKET_SEVER.getVoiceMsg()  );
                    }
                    else if( APP.TEXT_PANE.hasInput() ){
                        argList.addAll(  APP.TEXT_PANE.getTextInput()  );
                    }
                    

                    //判斷使用者是不是要翻頁
                    if( checkTurnPage( argList.getFirst() ) ){
                        argList.removeFirst();
                        continue;
                    }

                    //-----------------------------------------------------------------
                    //假如現在是 DO_NOTHING 就判斷 command
                    if( nowCommand instanceof DoNothingCommand ){
                        //隱藏圖片資訊欄
                        if( APP.MAIN.isNowHaveSelected() == true ){
                            APP.MAIN.getNowSelectedImageObject().hideInfoMenu();
                        }
                        //判斷是否有包含這個指令
                        Command tempCmd = commandMap.get( argList.getFirst() );
                        if( tempCmd != null ){
                            //移除第一個指令，只剩下數值
                            argList.removeFirst();

                            //指令階段初始化
                            tempCmd.initial();
                            NextCommand nextCommand = tempCmd.execute( "" );

                            //假如 NextCommand 的 isStaak 為 true，代表要將上個指令推入推疊裡，以方便下次執行
                            if( nextCommand.isStack() ){
                                commandStack.push( tempCmd );
                            }

                            //取得下個指令
                            nowCommand = nextCommand.toCommand( commandMap );

                            //傳送目前狀態給 Python Socket
                            APP.SOCKET_SEVER.sendStatus( nowCommand.getClass().getSimpleName() );

                            //更改訊息狀態顯示
                            APP.STATUS_PANE.changeBothText( nowCommand.getChineseName(), nowCommand.getPhase().getName() );
                        
                        }
                        else {
                            //將 LinkedList 轉成 ArrayList 陣列
                            String args[] = new String [ argList.size() ];
                            runTemporaryCommand( argList.toArray( args ) );

                            System.out.println( ConsoleColors.WARNING + "Command Not Found！" );
                            argList.clear(); 
                        }
                    }
                }
                //執行指令的地方
                runCommand();

                //latch.await(); 
                //Thread.sleep( 500 );
                APP.STATUS_PANE.changeBothText("", "指令");

                //傳送目前狀態給 Python Socket
                APP.SOCKET_SEVER.sendStatus( nowCommand.getClass().getSimpleName() );
            }
            
        }catch( Exception e ){
            e.printStackTrace();
            Platform.exit();
        }
        return null;
    }

    /** 最主要執行指令的地方 */
    private void runCommand() throws Exception{
        //判斷是否要執行(當有輸入的時候才會設為 true)
        boolean executeFlag = true;
        //做到直到變成 DO_NOTHING    
        while( !(nowCommand instanceof DoNothingCommand)  ){
            //判斷 argList 裡面有沒有 String 有才開始做
            if( executeFlag && argList.size() > 0 ){
                System.out.println( ConsoleColors.ACTION + nowCommand.getClass().getSimpleName() );

                //將 LinkedList 轉成 ArrayList 陣列
                String args[] = new String [ argList.size() ];
                //取得下個指令 NextCommand
                NextCommand nextCommand = nowCommand.execute( argList.toArray(args) );

                //假如 NextCommand 的 isStaak 為 true，代表要將上個指令推入推疊裡，以方便下次執行
                if( nextCommand.isStack() ){
                    commandStack.push( nowCommand );
                }

                //假如上個指令有設定 要把輸入清空 就把原有 List 清空
                if( nextCommand.isClearArgs() ){
                    argList.clear();
                }

                //先取得下一個的指令
                Command tempCMD = nextCommand.toCommand( commandMap );
                if( tempCMD instanceof DoNothingCommand ){
                    saveCommandToTemp( nowCommand );
                }

                executeFlag = false;

                //取得下個指令 Command
                nowCommand = tempCMD;

                //更改訊息狀態顯示
                APP.STATUS_PANE.changeStatusText( nowCommand.getPhase().getName() );
            }
            //判斷 SocketServer 有沒有讀入字串
            else if( APP.SOCKET_SEVER.isReceived() || APP.TEXT_PANE.hasInput() ){

                if( APP.SOCKET_SEVER.isReceived() ){

                    //假如他是垃圾訊息就印出所有字串 (不執行)
                    if( APP.SOCKET_SEVER.isSpamMsg() ){
                        String temp = APP.SOCKET_SEVER.getFormatWholeMsg();
                        if(!temp.contains("超過時間")) APP.INFO_PANE.println( temp );
                        continue;
                    }

                    //判斷 Python 讀入的字串是否有多個，有多個就直接存到 List 裡
                    argList.addAll(  APP.SOCKET_SEVER.getVoiceMsg()  );
                }
                else if( APP.TEXT_PANE.hasInput() ){
                    argList.addAll(  APP.TEXT_PANE.getTextInput()  );
                }

                //判斷使用者是不是要翻頁
                if( checkTurnPage( argList.getFirst() ) && nowCommand.getPhase() != Phase.Get_Web_Keyword ){
                    argList.removeFirst();
                    continue;
                }

                //有接收到才可以執行
                executeFlag = true;
            }
            else {
                System.out.println( ConsoleColors.STATUS + nowCommand.getClass().getSimpleName() + " " + nowCommand.getPhase().toString() );
                drawbyMove();
                //Delay
                Thread.sleep( 500 );
                APP.STATUS_PANE.animation();
                APP.SOCKET_SEVER.getFloatingPane().animation();
            }
        }
        //清空 args 暫存陣列
        argList.clear();
    }

    /** 執行暫存指令 
     *  @param args 參數*/
    private void runTemporaryCommand( String ... args ){
        for ( Command cmd : tempCommandList ) {
            if( cmd.doTemporaryAction( args ) != null ){
                break;
            }
        }
    }
    /** 將指令存進暫存裡 */
    private void saveCommandToTemp( Command cmd ){
        tempCommandList.remove( cmd );
        tempCommandList.add( 0, cmd );
    }
    //≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡
    final String nextPage = "下一頁";
    final String prevPage = "上一頁";
    final String firstPage = "第一頁";
    final String lastPage = "最後一頁";
    /** 判斷是否使用者要翻頁 
     *  @param arg 欲判斷的字串 
     *  @return {@code true = 成功翻頁} | {@code false = 未進行翻頁}*/
    public boolean checkTurnPage( String arg ){
        //先判斷有沒有包含 Page
        if( !arg.contains("頁") )return false;

        String msg = "";
        PagePane nowPagePane = APP.MAIN.getNowPagePane();
        //下一頁
        if( arg.equalsIgnoreCase( nextPage ) ){
            msg = nowPagePane.turnPage( PagePane.PAGE_NEXT );
        }
        //上一頁
        else if( arg.equalsIgnoreCase( prevPage ) ){
            msg = nowPagePane.turnPage( PagePane.PAGE_PREVIOUS );
        }
        //第一頁
        else if( arg.equalsIgnoreCase( firstPage ) ){
            msg = nowPagePane.turnPage( PagePane.PAGE_FIRST );
        }
        //最後一頁
        else if( arg.equalsIgnoreCase( lastPage ) ){
            msg = nowPagePane.turnPage( PagePane.PAGE_LAST );
        }
        //跳頁
        else if( arg.split("$").length >= 2 ){
            try{
                int page = Integer.parseInt( arg.split("$")[1] );
                msg = nowPagePane.jumpPage( page );
            }catch( Exception e ){ return false; }
        }
        else return false;
        System.out.println( ConsoleColors.INFO + msg );

        return true;
    }

    
    /** 語音畫圖移動 */
    public void drawbyMove(){
        try{
            Tool tool=APP.TOOL_BAR.getNowTool();
            if (nowCommand.getClass().getSimpleName().equals("DrawCommand")
                    && nowCommand.getPhase().toString().equals("Get_Value")
                    && tool instanceof PencilTool) {
                if(((PencilTool) tool).isMove())
                ((PencilTool) tool).move();
                if(((PencilTool) tool).isOutBound())
                APP.INFO_PANE.println("到達邊界");
            }
            if (nowCommand.getClass().getSimpleName().equals("DrawCommand")
                    && nowCommand.getPhase().toString().equals("Get_Value")
                    && tool instanceof EraserTool) {
                if(((EraserTool) tool).isMove())
                ((EraserTool) tool).move();
                if(((EraserTool) tool).isOutBound())
                APP.INFO_PANE.println("到達邊界");
            }
        }catch(Exception e){e.printStackTrace();}
    }
}