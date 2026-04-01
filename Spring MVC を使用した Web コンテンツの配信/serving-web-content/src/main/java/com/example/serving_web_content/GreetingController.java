package com.example.serving_web_content;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/* 
 * このクラスはコントローラであると宣言する 
 * @GetMapping注釈により、HTTP GET リクエストがメソッド/greetingにマッピングされることが保証されますgreeting()
 */
@Controller
public class GreetingController {
  /*
   * HTTP GETリクエストで
   * ブラウザで /greeting にアクセスされたとき、このメソッドが呼び出される
   */
  @GetMapping("/greeting")
  public String greeting(
  /*
    * @RequestParam は、URLの ?name=... の値を受け取るためのものです。
    *
    * 例:
    * /greeting?name=Foo
    * → name には "Foo" が入る
    *
    * required = false
    * → name がなくてもエラーにしない
    *
    * defaultValue = "World"
    * → name が指定されなかったら "World" を使う
    */
    @RequestParam(name="name", required=false, defaultValue="World") String name,
   /*
    * Model は、Java側からHTML側へ値を渡すための入れ物です。
    * 今回は "name" という名前で値を入れて、
    * greeting.html で使えるようにします。
    */
    Model model){
  /* 
   * HTML側へ "name" という名前で値を渡しています。
   * 例えば name = "Foo" のとき、
   * HTML側では ${name} と書くと "Foo" を取り出せます。 
   * 第二引数はString nameで受け取った値をセット。
   */
   model.addAttribute("name", name);
   /*
    * templates フォルダの中にある greeting.html を表示する、
    * ファイル名そのものを書いているわけではなくView名（画面名）を返しています。
    */
   return "greeting";
  }
}
