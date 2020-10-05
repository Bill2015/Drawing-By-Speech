package talkdraw.command;


import talkdraw.App;
import talkdraw.ImageViewPane;
import talkdraw.Main;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;
import talkdraw.imgobj.ImageObject;

/** 【指令】《網路相關指令》
    * <p>將目前的圖片更換成網路的圖片</p>
    *  @param args_0 網路圖片 ID 
    *  @param args_0 網路圖片 ID */
public class ChangeImageFromLocalCommand extends Command{

    /** 建構子 */
    public ChangeImageFromLocalCommand(App APP, CommandAttribute yamlLoader){
        super(APP, yamlLoader);
    }
    @Override
    public NextCommand execute( String ... args ) {

        switch ( phase ) {
            case None:
                //假如目前沒有選擇出預設的圖片就導向至 Select Image
                if( APP.MAIN.isNowHaveSelected() == false ){
                    return new NextCommand( SelectImageCommand.class, Phase.Get_Name_Or_Tag, true, false );
                }
                else{
                    //取得目前操作的圖片
                    ImageObject imgObj = APP.MAIN.getNowSelectedImageObject();
                    //將目前的圖片關鍵字拿去搜尋
                    if( APP.MAIN.getLocalConnection().searchImages( imgObj.getName() ) <= 0 ){
                        return failure( "未找到任何關於 ", imgObj.getName(), " 的圖片" );
                    }
                    //切換右側選單至網路圖片
                    APP.IMAGE_PANE.changeViewTabPane( ImageViewPane.LOCAL );
                    return new NextCommand( this.getClass(), Phase.Get_ID );
                }
            //------------------------
            case Get_ID:
                //判斷有無數值
                if( args.length < 1 )
                    return new NextCommand( this.getClass(), phase );
                
                //假如他不是輸入標邊
                else if( !args[0].matches("[a-zA-z]{1}[0-9]{1}") ){
                    //return getImageFromLocal( args[0] )?new NextCommand( this.getClass() , Phase.Get_ID, false, true ):failure( args[0] );
                    
                    String[] tempArgs = args[0].split("%");
                    if(tempArgs.length<2)
                        return getImageFromLocal( args[0] )?new NextCommand( this.getClass() , Phase.Get_ID, false, true ):failure( args[0] );
                    else
                        return getImageFromLocal( tempArgs[0] )?new NextCommand( this.getClass() , Phase.Get_ID, false, true ):getImageFromLocal( tempArgs[1] )?new NextCommand( this.getClass() , Phase.Get_ID, false, true ):failure( args[0] );
                }

                //取得 ID
                return getNetImageByID( args[0].toUpperCase() );
            //------------------------
            default:break;
        }

        return null;
    }

    @Override protected NextCommand success( String ... args ) {
        APP.println( args[0] , App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        APP.println( String.join("",args[0], args[1], args[2]) , App.PRINT_BOTH );
        //切換右側選單至預設
        APP.IMAGE_PANE.changeViewTabPane( ImageViewPane.DEFAULT );
        return NextCommand.DO_NOTHING;
    }
    @Override public NextCommand doTemporaryAction( String ... args ){ return null; }

    /** 取得網路上的圖片
     *  @param ID 欲取得的圖片編號 
     *  @return 回傳下一個狀態 {@code NextCommand}*/
    private NextCommand getNetImageByID( String ID ){        
        Main main = APP.MAIN;
        
        //取得目前使用者選擇的圖片
        ImageObject imgObj = APP.MAIN.getNowSelectedImageObject();
 
        //切換圖片
        if( !main.changeImageToLayer( imgObj, ID , Main.GET_MODE.LOCAL) ){
            return failure( "沒有編號為 ", ID, " 的圖片" );
        }

        //切換右側選單至預設
        APP.IMAGE_PANE.changeViewTabPane( ImageViewPane.DEFAULT );
        return success( "成功更換 " + imgObj.getName() );
    }

    /** 取得網路上的圖片
     *  @param keyword 欲取得的圖片關鍵字 
     *  @return 回傳下一個狀態 {@code NextCommand}*/
    /* private NextCommand getImageFromLocal( String keyword ){    
        //搜尋網路圖片
        int size = APP.MAIN.searchImageFromLocal( keyword );

        //更新右側的 ImageViewPane
        APP.IMAGE_PANE.updateViewBox();

        if( size <= 0 ){
            return failure( keyword );
        }
        success( String.join(" ",  "找到關於", keyword, "的圖片 共", Integer.toString(size), "個" ) );
      
        return new NextCommand( this.getClass() , Phase.Get_ID, false, true );
    } */
    /** 取得網路上的圖片
     *  @param keyword 欲取得的圖片關鍵字 
     *  @return 回傳下一個狀態 {@code NextCommand}*/
    private boolean getImageFromLocal( String keyword ){    
        //搜尋網路圖片
        int size = APP.MAIN.getLocalConnection().searchImages( keyword );

        //更新右側的 ImageViewPane
        APP.IMAGE_PANE.updateViewBox();

        if( size <= 0 ){
            return false;//failure( keyword );
        }
        success( String.join(" ",  "找到關於", keyword, "的圖片 共", Integer.toString(size), "個" ) );
      
        return true;//new NextCommand( this.getClass() , Phase.Get_ID, false, true );
    }
}