package talkdraw.command;

import java.util.HashMap;

import javafx.application.Platform;
import talkdraw.App;
import talkdraw.ImageViewPane;
import talkdraw.command.base.Command;
import talkdraw.command.base.CommandAttribute;
import talkdraw.command.base.NextCommand;
import talkdraw.imgobj.ImageObject;

/**【指令】 《圖片相關指令》
 * <p>選擇圖片</p>
 * <p>目前是讓使用者只能選取出一張圖片</p>
 * <p>之後要改成可以選取多張圖片再改</p>
 *  @param args0 圖片名稱 or 標籤(標籤目前未製作)*/
public class SelectImageCommand extends Command{

    /** 建構子 */
    public SelectImageCommand(App APP, CommandAttribute yamlLoader){
        super(APP, yamlLoader);
    }
    @Override
    public NextCommand execute( String ... args ) {

        switch ( phase ) {
            case None:
                return new NextCommand( this.getClass(), Phase.Get_Name_Or_Tag );
            //------------------------
            case Get_Name_Or_Tag:
                if( args.length < 1 )
                    return new NextCommand( this.getClass(), phase );

                return getImageByName( args[0] );
            //------------------------
            case Get_ID:
                if( args.length < 2 )
                    return new NextCommand( this.getClass(), phase );

                return getSelectImageByID( args[1] );
            default:break;
        }

        return null;
    }

    @Override protected NextCommand success( String ... args ) {
        Platform.runLater(() -> APP.MAIN.getPickedImageList().forEach( obj -> obj.hideIDLabel() ) );
        APP.println( args[0], App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }

    @Override protected NextCommand failure( String ... args ) {
        Platform.runLater(() -> APP.MAIN.getPickedImageList().forEach( obj -> obj.hideIDLabel() ) );
        APP.println( args[0], App.PRINT_BOTH );
        return NextCommand.DO_NOTHING;
    }
    @Override public NextCommand doTemporaryAction( String ... args ){ return null; }

    /** 取得網路上的圖片
     *  @param word 欲取得的圖片名稱 or 標籤 
     *  @return 回傳下一個狀態 {@code NextCommand}*/
    private NextCommand getImageByName( String word ){      
        
        //取得中文與英文輸入，假如只有一個就只判斷一個
        String names[] = word.contains("%") ? word.split("%") : new String[]{word, "aaaaaaaaaaa"};
        //目前只有用名稱搜尋喔
        HashMap<String, ImageObject> res = APP.MAIN.getUnModifyAllImageList().getImageNameOrTags( names[0], names[1].toLowerCase() );
        APP.MAIN.setNowPickedImageObjectMap( res );
        //未找到相關圖片
        if( res.size() <= 0 )return failure( String.join("", "選擇失敗，未找到關於 ", names[0], " 的圖片") );
        else if( res.size() == 1 ){
            APP.MAIN.setNowSelectedImageObject( res.get("A0") );

            return success("成功找到圖片！" );
        }
        else{
            APP.IMAGE_PANE.changeViewTabPane( ImageViewPane.SELECT );
            APP.MAIN.setNowPickedImageObjectMap( res );
            //設定圖片暫存
            return new NextCommand( this.getClass(), Phase.Get_ID );
        }
    }

    private NextCommand getSelectImageByID( String id ){

        ImageObject imgobj = APP.MAIN.getPickedImageMap().get( id.toUpperCase() );

        APP.IMAGE_PANE.changeViewTabPane( ImageViewPane.DEFAULT );
        if( imgobj == null ){
            return failure( String.join("", "選擇失敗，未找到關於 ", id, " 的圖片") );
        }
        else{
            if( !APP.MAIN.getNowSelectedImageObject().equals( imgobj ) ){
                APP.MAIN.setNowSelectedImageObject( imgobj );
            }
            else{
                return failure( String.join("", "已經選擇 ", imgobj.getName(), " 的圖片") );
            }
        }


        return success("成功找到圖片！");
    }
}