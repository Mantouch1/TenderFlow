
-- searchByTitleAndStatus
SELECT t.*
FROM tenders t
JOIN tender_status s ON t.status_id = s.id
WHERE ('serveur' IS NULL OR LOWER(t.title) LIKE LOWER('%serveur%'))
  AND ('Publié' IS NULL OR LOWER(s.name) = LOWER('Publié'));

SELECT t.* 
FROM tenders t
JOIN tender_status s ON t.status_id = s.id
WHERE LOWER(t.title) LIKE '%serveur%' 
AND LOWER(s.name)= 'publié';

-- searchTendersByDateRange
SELECT *
FROM tenders
WHERE publication_date BETWEEN '2025-07-01' AND '2025-07-31';

-- searchTendersByCreatedBy
SELECT * FROM tenders t
WHERE t.created_by = 'itprocurement@example.com'

-- searchTenderWithdocumentAnbdStatus
SELECT 
  t.*,
  d.*,
  s.name AS status_name
FROM tenders t
JOIN tender_status s ON t.status_id = s.id
JOIN tender_documents d ON d.tender_id = t.id
WHERE LOWER(s.name) = LOWER('Clôturé');

-- searchByStatus
SELECT * FROM tenders t
JOIN tender_status s ON t.status_id = s.id
WHERE LOWER(s.name) = LOWER('Clôturé');

-- count document by status
SELECT
s."name" AS status_name,
COUNT(d.id) AS document_count
FROM tenders t
JOIN tender_status s ON t.status_id = s.id
LEFT JOIN tender_documents d ON d.tender_id = t.id
GROUP BY s.name



