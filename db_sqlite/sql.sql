/* главный запрос для получения всей характеристики определенного слова*/
SELECT id, word_ru, hebrew.word_he, transcriptions.word_tr, gender_ru, hebrew.gender_he, meanings.option, quantity FROM russians INNER JOIN hebrew ON hebrew.id = russians.hebrew_id INNER JOIN meanings ON meanings.id = russians.meaning_id INNER JOIN transcriptions ON transcriptions.id = hebrew.transcription_id;

/* главный запрос для получения всей характеристики определенного слова с ограничением*/
SELECT id, word_ru, hebrew.word_he, transcriptions.word_tr, gender_ru, hebrew.gender_he, meanings.option, quantity FROM russians INNER JOIN hebrew ON hebrew.id = russians.hebrew_id INNER JOIN meanings ON meanings.id = russians.meaning_id INNER JOIN transcriptions ON transcriptions.id = hebrew.transcription_id WHERE meanings.option = "noun";

/* запрос для спиннера*/
SELECT option FROM meanings;