INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Гениальное кино! Смотришь и думаешь «Так не бывает!», но позже понимаешь, что только так и должно быть. Начинаешь заново осмысливать значение фразы, которую постоянно используешь в своей жизни, «Надежда умирает последней». Ведь если ты не надеешься, то все в твоей жизни гаснет, не остается смысла. Фильм наполнен бесконечным числом правильных афоризмов. Я уверена, что буду пересматривать его сотни раз.'
FROM movies m
JOIN users u ON u.name = 'Дарлин Эдвардс'
WHERE m.name_russian = 'Побег из Шоушенка';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Кино это является, безусловно, «со знаком качества». Что же до первого места в рейтинге, то, думаю, здесь имело место быть выставление «десяточек» от большинства зрителей вкупе с раздутыми восторженными откликами кинокритиков. Фильм атмосферный. Он драматичный. И, конечно, заслуживает того, чтобы находиться довольно высоко в мировом кинематографе.'
FROM movies m
JOIN users u ON u.name = 'Габриэль Джексон'
WHERE m.name_russian = 'Побег из Шоушенка';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Перестал удивляться тому, что этот фильм занимает сплошь первые места во всевозможных кино рейтингах. Особенно я люблю когда при экранизации литературного произведение из него в силу специфики кинематографа исчезает ирония и появляется некий сверхреализм, обязанный удерживать зрителя у экрана каждую отдельно взятую секунду.'
FROM movies m
JOIN users u ON u.name = 'Рональд Рейнольдс'
WHERE m.name_russian = 'Зеленая миля';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Много еще можно сказать об этом шедевре. И то, что он учит верить, и то, чтобы никогда не сдаваться… Но самый главный девиз я увидел вот в этой фразе: «Занимайся жизнью, или занимайся смертью».'
FROM movies m
JOIN users u ON u.name = 'Нил Паркер'
WHERE m.name_russian = 'Форрест Гамп';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Очень хороший фильм, необычный сюжет, я бы даже сказала непредсказуемый. Такие фильмы уже стали редкостью.'
FROM movies m
JOIN users u ON u.name = 'Трэвис Райт'
WHERE m.name_russian = 'Список Шиндлера';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'У меня не найдётся слов, чтобы описать этот фильм. Не хочется быть банальной и говорить какой он отличный, непредсказуемый и т. д., но от этого никуда не деться к сожалению — ведь он ШЕДЕВРАЛЬНЫЙ!'
FROM movies m
JOIN users u ON u.name = 'Джесси Паттерсон'
WHERE m.name_russian = '1+1';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Скажу сразу — фильм выглядел многообещающе, даже если не брать в расчет что он находился в топе-250 лучших фильмов. Известные голливудские актеры на главных ролях. Но нет в этом фильме должной атмосферы. Нет такого чувства что вот сейчас случится что-то страшное. Что герои попали в ситуацию из которой не смогут выбраться. В общем, не понравилось.'
FROM movies m
JOIN users u ON u.name = 'Амелия Кэннеди'
WHERE m.name_russian = '1+1';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'«Все должно быть супер! Супер! Су-пер!» И это именно супер, ну слов других не подберешь.'
FROM movies m
JOIN users u ON u.name = 'Габриэль Джексон'
WHERE m.name_russian = 'Жизнь прекрасна';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Фильм очень красивый. Не во всем, конечно, но яркие персонажи и костюмы — это уже кое-что.'
FROM movies m
JOIN users u ON u.name = 'Деннис Крейг'
WHERE m.name_russian = 'Бойцовский клуб';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Этот фильм из разряда тех, что могут обеспечить хороший отдых и приподнятое настроение за счёт своей лёгкости, совсем непошлого юмора, умеренной дозы напряжения, динамики нужных скоростей.'
FROM movies m
JOIN users u ON u.name = 'Габриэль Джексон'
WHERE m.name_russian = 'Как приручить дракона';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Назначается Киношедевром среди развлекательных фильмов.'
FROM movies m
JOIN users u ON u.name = 'Нил Паркер'
WHERE m.name_russian = 'Как приручить дракона';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Фильм продуман до мельчайших деталей. Идеальный фильм для улучшения настроения, единственный в своем роде. Обязателен к просмотру!'
FROM movies m
JOIN users u ON u.name = 'Айда Дэвис'
WHERE m.name_russian = 'Как приручить дракона';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Данный кинофильм — нестареющая классика мирового кинематографа, который можно пересматривать до бесконечности и, что удивительно, он не может надоесть.'
FROM movies m
JOIN users u ON u.name = 'Амелия Кэннеди'
WHERE m.name_russian = 'Гладиатор';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Рекомендую смотреть всем и не обращать внимания на надоевшее уже спасение целого мира одним человеком.'
FROM movies m
JOIN users u ON u.name = 'Дэрил Брайант'
WHERE m.name_russian = 'Темный рыцарь';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Удивлен. Никто не отозвался плохо? Неужели было создано произведение искусства, которое нравится всем, и которое совершенно? Нет. Может, я один такой? Фильм не вызывает во мне никаких эмоций. Неплохая сказочка. Замечательная наивная атмосфера. Местами есть забавные шутки. И, как мне показалось, этот фильм — своего рода стёб над другими боевиками. При этом превосходящий многие боевики.'
FROM movies m
JOIN users u ON u.name = 'Дэрил Брайант'
WHERE m.name_russian = 'Большой куш';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Приятного просмотра для всех, кто не видел ещё этого шедевра больше впечатлений для тех, кто пересматривает в надцатый раз. =)'
FROM movies m
JOIN users u ON u.name = 'Трэвис Райт'
WHERE m.name_russian = 'Большой куш';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Это один из любимых моих фильмов с самого детства. Я видела его столько раз, что знаю практически наизусть. И могу сказать с уверенностью, что посмотрю еще не один раз.'
FROM movies m
JOIN users u ON u.name = 'Нил Паркер'
WHERE m.name_russian = 'Большой куш';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Необыкновенно позитивный фильм. Его можно пересматривать много раз для поднятия настроения, находя много смешных, незаметных на первый взгляд моментов.'
FROM movies m
JOIN users u ON u.name = 'Деннис Крейг'
WHERE m.name_russian = 'Унесённые призраками';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Легендарный. Культовый. Бессмертный. Три слова. Всего лишь три. А сколько же они выражают неподдельных эмоций и радостных впечатлений по отношению к очередному любимому и уважаемому фильму из минувшего в лету детства? Много. Слишком много. И описать эти сердечные и гарцующие в здравом рассудке чувства обыкновенными строчными предложениями иногда не представляется возможным.'
FROM movies m
JOIN users u ON u.name = 'Амелия Кэннеди'
WHERE m.name_russian = 'Звёздные войны: Эпизод 4 – Новая надежда';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Фильм, безусловно, посмотрела уже большая часть населения, которая хоть каким-то образом имеет отношение к кинематографу. Я считаю, что фильм можно пересмотреть еще не один раз.'
FROM movies m
JOIN users u ON u.name = 'Габриэль Джексон'
WHERE m.name_russian = 'Молчание ягнят';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Хоть и не по возрасту мне заводить скрипучую пластинку с мелодией «Раньше и деревья были выше, и трава зеленее…», а хочется. Выражать свою любовь к настолько близкому произведению крайне сложно.'
FROM movies m
JOIN users u ON u.name = 'Дарлин Эдвардс'
WHERE m.name_russian = 'Молчание ягнят';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Фильм потрясающий, в нем хватает абсолютно всего: и драк, и музыки, и юмора, и любви.'
FROM movies m
JOIN users u ON u.name = 'Нил Паркер'
WHERE m.name_russian = 'Блеф';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Вердикт: прекрасная, нестареющая классика, которая рекомендована мною для всех.'
FROM movies m
JOIN users u ON u.name = 'Айда Дэвис'
WHERE m.name_russian = 'Блеф';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'У фильма есть свои мелкие недостатки  и неточности, но многочисленные достоинства в несколько раз перевешивают. Много вдохновляющего креатива!'
FROM movies m
JOIN users u ON u.name = 'Рональд Рейнольдс'
WHERE m.name_russian = 'Гран Торино';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Для воскресного вечернего просмотра подходит по всем критериям.'
FROM movies m
JOIN users u ON u.name = 'Дэрил Брайант'
WHERE m.name_russian = 'Хороший, плохой, злой';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Хороший и по-настоящему интересный фильм, с хорошим сюжетом и неплохим музыкальным сопровождением. Всем советую к просмотру.'
FROM movies m
JOIN users u ON u.name = 'Нил Паркер'
WHERE m.name_russian = 'Укрощение строптивого';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Конечно, бесспорно культовый фильм, не реалистичный, наивный, где то глупый, но такой же увлекательный и удивительный, как и много лет назад'
FROM movies m
JOIN users u ON u.name = 'Джесси Паттерсон'
WHERE m.name_russian = 'Укрощение строптивого';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Полагаю, этот фильм должен быть в коллекции каждого уважающего себя киномана.'
FROM movies m
JOIN users u ON u.name = 'Трэвис Райт'
WHERE m.name_russian = 'Джанго освобожденный';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Фильм только выигрывает от частого просмотра и всегда поднимает мне настроение (наряду с драмой, тут еще и тонкий юмор).'
FROM movies m
JOIN users u ON u.name = 'Айда Дэвис'
WHERE m.name_russian = 'Джанго освобожденный';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Нетленный шедевр мирового кинематографа, который можно пересматривать десятки раз и получать все такой — же, извините за выражение, кайф от просмотра. Минусы у фильма, конечно, есть, но черт возьми. Их просто не хочется замечать! Ты настолько поглощен просмотром фильма, что просто не хочется придираться к разным мелочам.'
FROM movies m
JOIN users u ON u.name = 'Амелия Кэннеди'
WHERE m.name_russian = 'Танцующий с волками';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'В итоге мы имеем отличный представитель своего жанра, который прошёл проверку временем и до сих пор отлично смотрится, несмотря на классический сюжет'
FROM movies m
JOIN users u ON u.name = 'Джесси Паттерсон'
WHERE m.name_russian = 'Титаник';

INSERT INTO reviews (movie_id, author_id, comment)
SELECT m.id, u.id,
'Скажу только одно — как я жалею, что не посмотрела его раньше!'
FROM movies m
JOIN users u ON u.name = 'Деннис Крейг'
WHERE m.name_russian = 'Пролетая над гнездом кукушки';