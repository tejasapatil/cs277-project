sed -i 's/\\n/ /g' yelp_training_set_review.json 

awk '{ print $2" "  $1}' part-r-00000  > part1
sort -n part1 > part2
tail -1000 part2 > part3


business = load '/user/tejas/kaggle/training/reduced/business.json' using JsonLoader('business_id:chararray,open:chararray,category:chararray,review_count:chararray,stars:float');

user = load '/user/tejas/kaggle/training/reduced/user.json' using JsonLoader('user_id:chararray,useful_votes:chararray,review_count:chararray,stars:float');

review = load '/user/tejas/kaggle/training/reduced/review.json' using JsonLoader('review_id:chararray,business_id:chararray,user_id:chararray,useful_votes:chararray, stars:chararray,age:chararray,text_wc:chararray,tasti:chararray,grill:chararray, fish:chararray,servic:chararray,delici:chararray,nice:chararray,chocol:chararray, good:chararray,wait:chararray,salsa:chararray,awesom:chararray,long:chararray, love:chararray,decor:chararray,expect:chararray,doesn:chararray,free:chararray, help:chararray,more:chararray,deal:chararray,enjoi:chararray,becaus:chararray, lunch:chararray,right1:chararray,worth:chararray,cool:chararray,favorit:chararray, mexican:chararray,flavor:chararray,qualiti:chararray,fun:chararray,like:chararray,  few:chararray,huge:chararray,price:chararray,sweet:chararray,better:chararray, definit:chararray,pork:chararray,amaz:chararray,impress:chararray,hot:chararray, didn:chararray,big:chararray,fast:chararray,small:chararray,friend:chararray, littl:chararray,car:chararray,decent:chararray,other:chararray,well:chararray, recommend:chararray,super:chararray,style:chararray,fresh:chararray,fantast:chararray, meat:chararray,best:chararray,friendli:chararray,spici:chararray,won:chararray, especi:chararray,pretti:chararray,perfect:chararray,crowd:chararray,too:chararray, less:chararray,meal:chararray,steak:chararray,music:chararray,locat:chararray, disappoint:chararray,happi:chararray,shrimp:chararray,offer:chararray,new:chararray, great:chararray,experi:chararray,larg:chararray,special:chararray,high:chararray, waitress:chararray,week:chararray');

review_user = JOIN review BY user_id, user BY user_id;
business_review_user = JOIN review_user BY business_id, business BY business_id;

temp = foreach business_review_user generate review_user::review::review_id as review_id, review_user::review::business_id as business_id, review_user::review::user_id as user_id, review_user::review::useful_votes as review_useful_votes, review_user::review::stars as review_stars, review_user::review::age as review_age, review_user::review::text_wc as review_text_wc, review_user::user::useful_votes as user_useful_votes, review_user::user::review_count as user_review_count, review_user::user::stars as user_stars, business::open as business_open, business::category as category, business::review_count as review_count, business::stars as business::stars, review_user::review::won as won, review_user::review::chocol as chocol, review_user::review::doesn as doesn, review_user::review::shrimp as shrimp, review_user::review::style as style, review_user::review::less as less, review_user::review::fantast as fantast, review_user::review::car as car, review_user::review::salsa as salsa, review_user::review::especi as especi, review_user::review::fast as fast, review_user::review::deal as deal, review_user::review::crowd as crowd, review_user::review::impress as impress, review_user::review::grill as grill, review_user::review::music as music, review_user::review::fish as fish, review_user::review::pork as pork, review_user::review::steak as steak, review_user::review::waitress as waitress, review_user::review::decor as decor, review_user::review::great as great, review_user::review::high as high, review_user::review::spici as spici, review_user::review::mexican as mexican, review_user::review::like as like, review_user::review::decent as decent, review_user::review::super as super, review_user::review::larg as larg, review_user::review::disappoint as disappoint, review_user::review::fun as fun, review_user::review::qualiti as qualiti, review_user::review::good as good, review_user::review::week as week, review_user::review::huge as huge, review_user::review::cool as cool, review_user::review::free as free, review_user::review::offer as offer, review_user::review::worth as worth, review_user::review::perfect as perfect, review_user::review::expect as expect, review_user::review::long as long, review_user::review::meat as meat, review_user::review::tasti as tasti, review_user::review::sweet as sweet, review_user::review::help as help, review_user::review::awesom as awesom, review_user::review::special as special, review_user::review::big as big, review_user::review::hot as hot, review_user::review::small as small, review_user::review::recommend as recommend, review_user::review::favorit as favorit, review_user::review::amaz as amaz, review_user::review::happi as happi, review_user::review::enjoi as enjoi, review_user::review::definit as definit, review_user::review::meal as meal, review_user::review::experi as experi, review_user::review::new as new, review_user::review::few as few, review_user::review::fresh as fresh, review_user::review::right1 as right1, review_user::review::locat as locat, review_user::review::delici as delici, review_user::review::flavor as flavor, review_user::review::lunch as lunch, review_user::review::friend as friend, review_user::review::better as better, review_user::review::didn as didn, review_user::review::friendli as friendli, review_user::review::pretti as pretti, review_user::review::wait as wait, review_user::review::best as best, review_user::review::price as price, review_user::review::becaus as becaus, review_user::review::well as well, review_user::review::littl as littl, review_user::review::too as too, review_user::review::nice as nice, review_user::review::other as other, review_user::review::more as more, review_user::review::love as love, review_user::review::servic as servic;

STORE temp INTO '/user/tejas/kaggle/training/joined_data2' USING PigStorage (',');
-------------------------------------------------------------------------------------------------------------------------------
test_business = load '/user/tejas/kaggle/test/merged_business.json' using JsonLoader('business_id:chararray,categories:chararray,review_count:int,name:chararray,stars:float');

test_review_base = load '/user/tejas/kaggle/test/review.json' using JsonLoader('user_id:chararray,review_id:chararray,text:chararray,business_id:chararray,stars:int,date:chararray,type:chararray');
test_review = foreach test_review_base generate user_id as user_id, review_id as review_id, stars as stars, date as date, business_id as business_id;

test_user = load '/user/tejas/kaggle/test/user.json' using JsonLoader('review_count:int,name:chararray,average_stars:float,user_id:chararray');
train_user = load '/user/tejas/kaggle/training/user.json' using JsonLoader('votes:(funny:chararray,useful:chararray, cool:chararray),user_id:chararray,name:chararray,average_stars:float,review_count:int');

test_review_user = JOIN test_review BY user_id, train_user BY user_id;
test_business_review_user = JOIN test_review_user BY business_id, test_business BY business_id;

STORE test_business_review_user INTO '/user/tejas/kaggle/test/merged_data2' USING PigStorage (',');

result schema:
from review table   : user_id, review_id, stars, date, business_id 
from user table     : review_count, name, average_stars, user_id, type
from business table : business_id, categories, review_count, name, stars
